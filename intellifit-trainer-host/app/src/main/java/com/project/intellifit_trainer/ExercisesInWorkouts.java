package com.project.intellifit_trainer;

public class ExercisesInWorkouts {
    private String name;
    private int repCount;
    private int setCount;

    public ExercisesInWorkouts() {
        // Firebase Realtime Database için boş bir constructor
    }

    // getter ve setter metotları
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRepCount() {
        return repCount;
    }

    public void setRepCount(int repCount) {
        this.repCount = repCount;
    }

    public int getSetCount() {
        return setCount;
    }

    public void setSetCount(int setCount) {
        this.setCount = setCount;
    }
}

