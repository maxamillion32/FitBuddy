package de.avalax.fitbuddy.application;

import com.google.gson.Gson;
import de.avalax.fitbuddy.domain.model.exercise.BasicExercise;
import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.set.BasicSet;
import de.avalax.fitbuddy.domain.model.set.Set;
import de.avalax.fitbuddy.domain.model.workout.BasicWorkout;
import de.avalax.fitbuddy.domain.model.workout.Workout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WorkoutFactory {
    Gson gson = new Gson();

    public Workout createFromJson(String contents) {
        if (contents == null || contents.isEmpty()) {
            throw new WorkoutParseException();
        }
        try {
            List s = gson.fromJson(contents, ArrayList.class);
            List<ArrayList> exercises = (ArrayList) s.get(1);
            LinkedList<Exercise> exerciseList = new LinkedList<>();
            for (ArrayList exercise : exercises) {
                List<Set> sets = new ArrayList<>();
                for (int i = 0; i < (double) exercise.get(2); i++) {
                    double weight = (double) exercise.get(3);
                    int maxReps = (int) ((double) exercise.get(1));
                    sets.add(new BasicSet(weight, maxReps));
                }
                exerciseList.add(new BasicExercise((String) exercise.get(0), sets));
            }
            return new BasicWorkout((String) s.get(0),exerciseList);
        } catch (RuntimeException re) {
            throw new WorkoutParseException(re);
        }
    }

    public Workout createNew() {
        LinkedList<Exercise> exercises = new LinkedList<>();
        return new BasicWorkout("", exercises);
    }
}
