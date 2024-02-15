package com.project.intellifit_trainer;

import java.util.List;

public class Workout {
    private String workoutName;
    private List<ExercisesInWorkouts> exercises;
    private boolean isSelected;

    public Workout() {
        // Firebase Realtime Database için boş bir constructor
    }

    public String getWorkoutName() {return workoutName;}

    public void setWorkoutName(String workoutName) {this.workoutName = workoutName;}

    public List<ExercisesInWorkouts> getExercises() {return exercises;}

    public void setExercises(List<ExercisesInWorkouts> exercises) {this.exercises = exercises;}

    public boolean isSelected() {return isSelected;}

    public void setSelected(boolean selected) {isSelected = selected;}
}

