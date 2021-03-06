package de.avalax.fitbuddy.port.adapter.persistence;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import de.avalax.fitbuddy.BuildConfig;
import de.avalax.fitbuddy.R;
import de.avalax.fitbuddy.domain.model.exercise.BasicExercise;
import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.exercise.ExerciseId;
import de.avalax.fitbuddy.domain.model.exercise.ExerciseRepository;
import de.avalax.fitbuddy.domain.model.set.BasicSet;
import de.avalax.fitbuddy.domain.model.set.Set;
import de.avalax.fitbuddy.domain.model.set.SetId;
import de.avalax.fitbuddy.domain.model.set.SetRepository;
import de.avalax.fitbuddy.domain.model.workout.BasicWorkout;
import de.avalax.fitbuddy.domain.model.workout.Workout;
import de.avalax.fitbuddy.domain.model.workout.WorkoutRepository;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = "src/main/AndroidManifest.xml", sdk = 21)
public class SQLiteSetRepositoryTest {

    private SetRepository setRepository;

    private ExerciseId exerciseId;

    private Set createSet(double weight, int maxReps) {
        Set set = new BasicSet();
        set.setWeight(weight);
        set.setMaxReps(maxReps);
        setRepository.save(exerciseId, set);
        return set;
    }

    @Before
    public void setUp() throws Exception {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        FitbuddySQLiteOpenHelper sqLiteOpenHelper = new FitbuddySQLiteOpenHelper("SQLiteSetRepositoryTest", 1, context, R.raw.fitbuddy_db);
        setRepository = new SQLiteSetRepository(sqLiteOpenHelper);
        ExerciseRepository exerciseRepository = new SQLiteExerciseRepository(sqLiteOpenHelper, setRepository);
        WorkoutRepository workoutRepository = new SQLiteWorkoutRepository(sqLiteOpenHelper, exerciseRepository);
        Workout workout = new BasicWorkout();
        Exercise exercise = new BasicExercise();
        workout.addExercise(0, exercise);

        workoutRepository.save(workout);
        exerciseId = exercise.getExerciseId();
    }

    @Test
    public void saveUnpersistedSet_shouldAssignNewSetId() throws Exception {
        Set set = new BasicSet();

        assertThat(set.getSetId(), nullValue());
        setRepository.save(exerciseId, set);

        assertThat(set.getSetId(), any(SetId.class));
    }

    @Test
    public void savePersistedSet_shouldKeepSetId() {
        Set set = createSet(42, 12);
        SetId setId = set.getSetId();

        setRepository.save(exerciseId, set);
        assertThat(set.getSetId(), equalTo(setId));
    }

    @Test
    public void loadAllSetsBelongsTo_shouldReturnSetsOfExercise() throws Exception {
        Set set1 = createSet(42, 12);
        Set set2 = createSet(40, 10);

        List<Set> sets = setRepository.allSetsBelongsTo(exerciseId);

        assertThat(sets.size(), equalTo(2));
        assertThat(sets.get(0), equalTo(set1));
        assertThat(sets.get(1), equalTo(set2));
    }

    @Test
    public void updateSets_shouldSaveTheCorrectEntity() {
        Set set1 = createSet(42, 12);
        createSet(40, 10);

        List<Set> sets = setRepository.allSetsBelongsTo(exerciseId);
        Set loadedSet2 = sets.get(1);
        loadedSet2.setWeight(100);

        setRepository.save(exerciseId, loadedSet2);

        List<Set> reloadedSets = setRepository.allSetsBelongsTo(exerciseId);
        assertThat(reloadedSets.get(0).getWeight(), equalTo(set1.getWeight()));
        assertThat(reloadedSets.get(1).getWeight(), equalTo(loadedSet2.getWeight()));
    }

    @Test
    public void deleteSetWithNull_shouldDoNothing() throws Exception {
        setRepository.delete(null);
    }

    @Test
    public void deleteSetBySetId_shouldRemoveItFromPersistence() throws Exception {
        double weight = 42;
        int maxReps = 12;
        Set set = createSet(weight, maxReps);
        SetId setId = set.getSetId();

        setRepository.delete(setId);
        assertThat(setRepository.allSetsBelongsTo(exerciseId).size(), equalTo(0));
    }
}