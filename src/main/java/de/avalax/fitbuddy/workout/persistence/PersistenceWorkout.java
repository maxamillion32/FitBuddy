package de.avalax.fitbuddy.workout.persistence;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.avalax.fitbuddy.workout.Exercise;
import de.avalax.fitbuddy.workout.Set;
import de.avalax.fitbuddy.workout.Tendency;
import de.avalax.fitbuddy.workout.Workout;

@Singleton
public class PersistenceWorkout implements Workout{
    private Workout workout;

    @Inject
    public PersistenceWorkout(DataLayer dataLayer) {
        workout = dataLayer.load();
    }

    @Override
    public Exercise getExercise(int index) {
        return workout.getExercise(index);
    }

    @Override
    public Set getCurrentSet(int index) {
        return workout.getCurrentSet(index);
    }

    @Override
    public String getName(int index) {
        return workout.getName(index);
    }

    @Override
    public float getProgress(int index) {
        return workout.getProgress(index);
    }

    @Override
    public void addExerciseBefore(int index, Exercise exercise) {
        //TODO: delegate to datalayer
        workout.addExerciseBefore(index, exercise);
    }

    @Override
    public void addExerciseAfter(int index, Exercise exercise) {
        //TODO: delegate to dataLayer
        workout.addExerciseAfter(index, exercise);
    }

    @Override
    public void setExercise(int index, Exercise exercise) {
        //TODO: delegate to dataLayer
        workout.setExercise(index,exercise);
    }

    @Override
    public void removeExercise(int index) {
        //TODO: delegate to dataLayer
        workout.removeExercise(index);
    }

    @Override
    public int getReps(int index) {
        return workout.getReps(index);
    }

    @Override
    public void setReps(int index, int reps) {
        workout.setReps(index, reps);
    }

    @Override
    public void setTendency(int index, Tendency tendency) {
        workout.setTendency(index, tendency);
    }

    @Override
    public void incrementSet(int index) {
        workout.incrementSet(index);
    }

    @Override
    public int getExerciseCount() {
        return workout.getExerciseCount();
    }
}
