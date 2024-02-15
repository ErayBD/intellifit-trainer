package com.project.intellifit_trainer;

import java.util.UUID;

public class WorkoutExercise {
    private String id; // Benzersiz tanımlayıcı
    private String name;
    private String muscleGroup;
    private int imageResourceId;
    private int repCount;
    private int setCount;
    private boolean isAdded;

    public WorkoutExercise(String name, String muscleGroup, int imageResourceId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public int getRepCount() {return repCount;}

    public void setRepCount(int repCount) {this.repCount = repCount;}

    public int getSetCount() {return setCount;}

    public void setSetCount(int setCount) {this.setCount = setCount;}

    public boolean isAdded() {return isAdded;}

    public void setAdded(boolean added) {isAdded = added;}

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}
}
