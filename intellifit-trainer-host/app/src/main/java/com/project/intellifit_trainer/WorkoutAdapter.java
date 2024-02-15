package com.project.intellifit_trainer;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<WorkoutExercise> workoutExercises;
    private OnItemClickListener onItemClickListener;
    private List<WorkoutExercise> addedExercises = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(WorkoutExercise workoutExercise);
    }

    public WorkoutAdapter(List<WorkoutExercise> workoutExercises, OnItemClickListener onItemClickListener) {
        this.workoutExercises = workoutExercises;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutExercise workoutExercise = workoutExercises.get(position);
        holder.textViewExerciseName.setText(workoutExercise.getName());
        holder.textViewMuscleGroup.setText(workoutExercise.getMuscleGroup());
        holder.imageViewExercise.setImageResource(workoutExercise.getImageResourceId());

        updateButtonState(holder.buttonSelect, workoutExercise.isAdded());

        holder.buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String repCountStr = holder.editTextRepCount.getText().toString();
                String setCountStr = holder.editTextSetCount.getText().toString();
                int repCount = 0;
                int setCount = 0;

                if (!TextUtils.isEmpty(repCountStr)) {
                    repCount = Integer.parseInt(repCountStr);
                    if (repCount < 3 || repCount > 30) {
                        holder.editTextRepCount.setError("The number of rep should be between 3 and 30.");
                        holder.editTextRepCount.requestFocus();
                        return;
                    }
                } else {
                    holder.editTextRepCount.setError("This field cannot be empty.");
                    holder.editTextRepCount.requestFocus();
                    return;
                }

                if (!TextUtils.isEmpty(setCountStr)) {
                    setCount = Integer.parseInt(setCountStr);
                    if (setCount < 1 || setCount > 10) {
                        holder.editTextSetCount.setError("The number of set should be between 1 and 10.");
                        holder.editTextSetCount.requestFocus();
                        return;
                    }
                } else {
                    holder.editTextSetCount.setError("This field cannot be empty.");
                    holder.editTextSetCount.requestFocus();
                    return;
                }

                boolean isAdded = workoutExercise.isAdded();
                if (isAdded) {
                    // Egzersizi listeden kaldır
                    workoutExercise.setAdded(false);
                    addedExercises.remove(workoutExercise);
                    Toast.makeText(view.getContext(), "Removed: " + workoutExercise.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    // Egzersizi listeye ekle
                    workoutExercise.setAdded(true);
                    workoutExercise.setRepCount(repCount);
                    workoutExercise.setSetCount(setCount);
                    addedExercises.add(workoutExercise);
                    Toast.makeText(view.getContext(), "Added: " + workoutExercise.getName(), Toast.LENGTH_SHORT).show();
                }
                updateButtonState(holder.buttonSelect, !isAdded);
            }
        });

    }

    @Override
    public int getItemCount() {
        return workoutExercises.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewExercise;
        TextView textViewExerciseName;
        TextView textViewMuscleGroup;
        Button buttonSelect;
        EditText editTextRepCount;
        EditText editTextSetCount;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            imageViewExercise = itemView.findViewById(R.id.workout_logo);
            textViewExerciseName = itemView.findViewById(R.id.workout_tv_exerciseName);
            textViewMuscleGroup = itemView.findViewById(R.id.workout_tv_muscleGroup);
            buttonSelect = itemView.findViewById(R.id.workout_bt_select);
            editTextRepCount = itemView.findViewById(R.id.workout_et_repCount);
            editTextSetCount = itemView.findViewById(R.id.workout_et_setCount);

        }
    }

    public List<WorkoutExercise> getAddedExercises() {
        return addedExercises;
    }

    private void updateButtonState(Button button, boolean isAdded) {
        if (isAdded) {
            button.setText("Remove");
            button.setBackgroundResource(R.drawable.button_remove); // R.drawable.button_remove ile tanımladığınız arkaplanı kullanın
        } else {
            button.setText("Add");
            button.setBackgroundResource(R.drawable.button_add); // R.drawable.button_add ile tanımladığınız arkaplanı kullanın
        }
    }

    private void removeExerciseFromAddedList(String exerciseId) {
        if (exerciseId == null) {
            return; // ID null ise işlem yapma
        }
        for (int i = 0; i < addedExercises.size(); i++) {
            WorkoutExercise exercise = addedExercises.get(i);
            if (exercise.getId().equals(exerciseId)) {
                addedExercises.remove(i);
                break; // Eşleşen egzersiz bulunduğunda döngüden çık
            }
        }
    }




    private boolean isExerciseAlreadyAdded(WorkoutExercise exercise) {
        for (WorkoutExercise addedExercise : addedExercises) {
            if (exercise.getId().equals(addedExercise.getId())) {
                return true; // Egzersiz zaten listede var
            }
        }
        return false; // Egzersiz listede yok
    }
}
