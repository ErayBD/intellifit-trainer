package com.project.intellifit_trainer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder> {

    private List<Exercise> exercisesList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ExercisesAdapter(List<Exercise> exercisesList) {
        this.exercisesList = exercisesList;
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, int position) {
        Exercise exercise = exercisesList.get(position);
        holder.textViewExerciseName.setText(exercise.getName());

        Glide.with(holder.imageViewExerciseGif.getContext())
                .load(exercise.getGifUrl())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .centerCrop()
                .into(holder.imageViewExerciseGif);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(exercise);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return exercisesList.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewExerciseGif;
        TextView textViewExerciseName;

        public ExerciseViewHolder(View itemView) {
            super(itemView);
            imageViewExerciseGif = itemView.findViewById(R.id.imageViewExerciseGif);
            textViewExerciseName = itemView.findViewById(R.id.textViewExerciseName);
        }
    }
}

