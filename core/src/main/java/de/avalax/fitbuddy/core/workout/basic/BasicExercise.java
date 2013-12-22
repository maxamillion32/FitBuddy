package de.avalax.fitbuddy.core.workout.basic;

import de.avalax.fitbuddy.core.workout.Exercise;
import de.avalax.fitbuddy.core.workout.Set;
import de.avalax.fitbuddy.core.workout.Tendency;

import java.util.List;

public class BasicExercise implements Exercise {
    private final String name;
    private final List<Set> sets;
    private double weightRaise;
    private Tendency tendency;
    private int setNumber;

    public BasicExercise(String name, List<Set> sets, double weightRaise) {
        this.name = name;
        this.sets = sets;
        this.weightRaise = weightRaise;
        this.setNumber = 1;
        this.tendency = Tendency.NEUTRAL;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxSets() {
        return sets.size();
    }

    @Override
    public int getSetNumber() {
        return setNumber;
    }

    @Override
    public Tendency getTendency() {
        return tendency;
    }

    @Override
    public void setTendency(Tendency tendency) {
        this.tendency = tendency;
    }

    @Override
    public Set getCurrentSet() {
        int index = setNumber - 1;
        return sets.get(index);
    }

    @Override
    public void setCurrentSet(int setNumber) {
        int index = setNumber - 1;
        if (isIndexGreaterEqualThan(index)) {
            this.setNumber = getMaxSets();
        } else if (isIndexNegative(index)) {
            this.setNumber = 1;
        } else {
            this.setNumber = setNumber;
        }
    }

    @Override
    public int getReps() {
        return getCurrentSet().getReps();
    }

    @Override
    public void setReps(int reps) {
        getCurrentSet().setReps(reps);
    }

    @Override
    public int getMaxReps() {
        return getCurrentSet().getMaxReps();
    }

    public void incrementCurrentSet() {
        setCurrentSet(setNumber + 1);
    }

    @Override
    public double getWeight() {
        return getCurrentSet().getWeight();
    }

    @Override
    public double getWeightRaise() {
        return weightRaise;
    }

    @Override
    public double getWeightRaise(Tendency tendency) {
        double weight = getCurrentSet().getWeight();
        if (tendency == Tendency.PLUS) {
            return getPlusTendencyWeight(weight);
        }
        if (tendency == Tendency.MINUS) {
            return getMinusTendencyWeight(weight);
        }
        return weight;
    }

    private boolean isIndexGreaterEqualThan(int index) {
        return index >= sets.size();
    }

    private boolean isIndexNegative(int index) {
        return index < 0;
    }

    private double getPlusTendencyWeight(double weight) {
        return weight + weightRaise;
    }

    private double getMinusTendencyWeight(double weight) {
        if (weight - weightRaise < 0) {
            return 0;
        }
        return weight - weightRaise;
    }
}
