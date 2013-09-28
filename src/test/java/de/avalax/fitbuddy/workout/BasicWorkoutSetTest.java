package de.avalax.fitbuddy.workout;


import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;

public class BasicWorkoutSetTest {

    WorkoutSet workoutSet;
    List<Set> sets;

    @Before
    public void setUp() throws Exception{
        sets = new ArrayList<Set>();
        workoutSet = new BasicWorkoutSet("Bankdrücken",sets);
    }

    @Test
    public void BasicWorkoutSet_ShouldReturnNameBankdrücken() throws Exception {
        assertThat(workoutSet.getName(), equalTo("Bankdrücken"));
    }

    @Test
    public void BasicWorkoutSet_ShouldReturnNumberOfSets() throws Exception {
        sets.add(mock(Set.class));
        sets.add(mock(Set.class));
        sets.add(mock(Set.class));
        sets.add(mock(Set.class));

        assertThat(workoutSet.getNumberOfSets(),equalTo(4));
    }

    @Test
    public void BasicWorkoutSet_ShouldReturnTendencyPlus() throws Exception {
        workoutSet.setTendency(Tendency.PLUS);

        assertThat(workoutSet.getTendency(), equalTo(Tendency.PLUS));
    }

    @Test
    public void BasicWorkoutSet_ShouldReturnCurrentSetOnStartup() throws Exception {
        Set set = mock(Set.class);
        sets.add(set);

        assertThat(workoutSet.getCurrentSet(), equalTo(set));
    }

    @Test
    public void BasicWorkoutSet_ShouldReturnSecondSetAsCurrentSet() throws Exception {
        Set set = mock(Set.class);

        sets.add(mock(Set.class));
        sets.add(set);

        workoutSet.setSetNumber(2);

        assertThat(workoutSet.getCurrentSet(), equalTo(set));
    }

    @Test
    public void BasicWorkoutSet_ShouldReturnPreviousSet() throws Exception {
        Set set = mock(Set.class);

        sets.add(set);
        sets.add(mock(Set.class));

        workoutSet.setSetNumber(2);

        assertThat(workoutSet.getPreviousSet(), equalTo(set));
    }

    @Test
    public void BasicWorkoutSet_ShouldReturnNextSet() throws Exception {
        Set set = mock(Set.class);

        sets.add(mock(Set.class));
        sets.add(set);

        workoutSet.setSetNumber(1);

        assertThat(workoutSet.getNextSet(), equalTo(set));
    }

    @Test(expected = SetNotAvailableException.class)
    public void BasicWorkoutSet_ShouldThrowExceptionWithNoPreviousSetAvailable() throws Exception {
        sets.add(mock(Set.class));

        workoutSet.setSetNumber(1);

        workoutSet.getPreviousSet();
    }

    @Test(expected = SetNotAvailableException.class)
    public void BasicWorkoutSet_ShouldThrowExceptionWithNoNextSetAvailable() throws Exception {
        sets.add(mock(Set.class));

        workoutSet.setSetNumber(1);

        workoutSet.getNextSet();
    }
}
