package de.avalax.fitbuddy.application.manageWorkout;

import android.view.View;
import de.avalax.fitbuddy.application.ExerciseFactory;
import de.avalax.fitbuddy.application.WorkoutFactory;
import de.avalax.fitbuddy.application.WorkoutSession;
import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.exercise.ExerciseRepository;
import de.avalax.fitbuddy.domain.model.workout.BasicWorkout;
import de.avalax.fitbuddy.domain.model.workout.Workout;
import de.avalax.fitbuddy.domain.model.workout.WorkoutId;
import de.avalax.fitbuddy.domain.model.workout.WorkoutRepository;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class ManageWorkoutTest {

    @Mock
    private WorkoutRepository workoutRepository;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private WorkoutFactory workoutFactory;
    @Mock
    private ExerciseFactory exerciseFactory;
    @Mock
    private WorkoutSession workoutSession;
    @Spy
    private Workout workout = new BasicWorkout(new LinkedList<Exercise>());
    @InjectMocks
    private ManageWorkout manageWorkout;
    @Mock
    private Exercise exercise;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        workout.setWorkoutId(new WorkoutId("123456"));
    }

    @Test
    public void afterInitialization_shouldHideUnsavedChanges() throws Exception {
        assertThat(manageWorkout.unsavedChangesVisibility(), equalTo(View.GONE));
    }

    @Test
    public void createWorkout_shouldPersistTheCreatedWorkout() throws Exception {
        when(workoutFactory.createNew()).thenReturn(workout);
        manageWorkout.createWorkout();
        verify(workoutRepository).save(workout);
    }

    @Test
    public void createWorkoutFromJson_shouldPersistTheCreatedWorkout() throws Exception {
        when(workoutFactory.createFromJson("jsonstring")).thenReturn(workout);
        manageWorkout.createWorkoutFromJson("jsonstring");
        verify(workoutRepository).save(workout);
        assertThat(manageWorkout.unsavedChangesVisibility(), equalTo(View.GONE));
    }

    public class givenAWorkoutWithOneExercise {
        private WorkoutId workoutId;

        @Before
        public void setUp() throws Exception {
            workoutId = new WorkoutId("42");
            when(workoutRepository.load(workoutId)).thenReturn(workout);
            when(workout.getWorkoutId()).thenReturn(workoutId);
            workout.addExercise(exercise);

            manageWorkout.setWorkout(workoutId);
        }

        @Test
        public void deleteWorkout_shouldRemoveTheWorkoutFromThePersistence() throws Exception {
            manageWorkout.deleteWorkout();

            verify(workoutRepository).delete(workoutId);
            assertThat(manageWorkout.getWorkout(), equalTo(null));
            assertThat(manageWorkout.unsavedChangesVisibility(), equalTo(View.VISIBLE));
            assertThat(manageWorkout.hasDeletedWorkout(), equalTo(true));
        }

        @Test
        public void undoDeleteWorkout_shouldReinsertTheWorkoutToThePersistence() throws Exception {
            manageWorkout.deleteWorkout();

            manageWorkout.undoDeleteWorkout();

            verify(workoutRepository).save(workout);
            assertThat(manageWorkout.getWorkout(), equalTo(workout));
            assertThat(manageWorkout.unsavedChangesVisibility(), equalTo(View.GONE));
            assertThat(manageWorkout.hasDeletedWorkout(), equalTo(false));
        }

        @Test
        public void createExercise_shouldPersistTheExercise() throws Exception {
            when(exerciseFactory.createNew()).thenReturn(exercise);

            manageWorkout.createExercise();

            verify(exerciseRepository).save(workout.getWorkoutId(), exercise);
        }

        @Test
        public void createExerciseAfter_shouldPlaceTheExerciseAfterTheGiven() throws Exception {
            Exercise exerciseAfter = mock(Exercise.class);
            when(exerciseFactory.createNew()).thenReturn(exerciseAfter);

            manageWorkout.createExerciseAfter(exercise);

            assertThat(workout.getExerciseCount(), equalTo(2));
            assertThat(workout.getExercise(1), equalTo(exerciseAfter));
            verify(exerciseRepository).save(workout.getWorkoutId(), exerciseAfter, 1);
        }

        @Test
        public void createExerciseBefore_shouldPlaceTheExerciseBeforeTheGiven() throws Exception {
            Exercise exerciseAfter = mock(Exercise.class);
            when(exerciseFactory.createNew()).thenReturn(exerciseAfter);

            manageWorkout.createExerciseBefore(exercise);

            assertThat(workout.getExerciseCount(), equalTo(2));
            assertThat(workout.getExercise(0), equalTo(exerciseAfter));
            verify(exerciseRepository).save(workout.getWorkoutId(), exerciseAfter, 0);
        }

        public class exerciseManipulation {
            @Test
            public void deleteExercise_shouldDeleteThePersistdExercise() throws Exception {
                manageWorkout.deleteExercise(exercise);

                verify(workout).deleteExercise(exercise);
                verify(exerciseRepository).delete(exercise.getExerciseId());
                assertThat(manageWorkout.unsavedChangesVisibility(), equalTo(View.VISIBLE));
                assertThat(manageWorkout.hasDeletedExercise(), equalTo(true));
            }

            @Test
            public void undoDeleteExercise_shouldReinsertTheExerciseToThePersistence() throws Exception {
                manageWorkout.deleteExercise(exercise);
                manageWorkout.undoDeleteExercise();

                verify(exerciseRepository).save(workout.getWorkoutId(), exercise);
                verify(workout).addExercise(exercise);
                assertThat(manageWorkout.unsavedChangesVisibility(), equalTo(View.GONE));
                assertThat(manageWorkout.hasDeletedExercise(), equalTo(false));
            }

            @Test
            public void undoDeleteExercise_shouldReinsertTheExerciseAtOldPosition() throws Exception {
                Exercise exerciseToRestore = mock(Exercise.class);
                workout.addExercise(exerciseToRestore);
                workout.addExercise(mock(Exercise.class));

                manageWorkout.deleteExercise(exerciseToRestore);
                manageWorkout.undoDeleteExercise();

                assertThat(workout.getExercise(1), equalTo(exerciseToRestore));
            }

            @Test
            public void undoDeleteExerciseAfterDeleteAnWorkoutAndExercise_shouldReinsertTheExerciseToThePersistence() throws Exception {
                Workout deletedWorkout = mock(Workout.class);
                when(deletedWorkout.getWorkoutId()).thenReturn(new WorkoutId("21"));
                when(workoutFactory.createNew()).thenReturn(deletedWorkout);
                manageWorkout.createWorkout();

                manageWorkout.deleteWorkout();
                assertThat(manageWorkout.hasDeletedWorkout(), equalTo(true));

                manageWorkout.setWorkout(workout.getWorkoutId());
                manageWorkout.deleteExercise(exercise);
                assertThat(manageWorkout.hasDeletedWorkout(), equalTo(false));
                manageWorkout.undoDeleteExercise();
                assertThat(manageWorkout.hasDeletedExercise(), equalTo(false));
            }

            @Test
            public void undoDeleteWorkoutAfterDeleteAnExerciseAndWorkout_shouldReinsertTheWorkoutToThePersistence() throws Exception {
                manageWorkout.deleteExercise(exercise);
                assertThat(manageWorkout.hasDeletedExercise(), equalTo(true));

                manageWorkout.deleteWorkout();
                assertThat(manageWorkout.hasDeletedExercise(), equalTo(false));

                manageWorkout.undoDeleteWorkout();
                assertThat(manageWorkout.hasDeletedExercise(), equalTo(false));
            }
        }
    }
}