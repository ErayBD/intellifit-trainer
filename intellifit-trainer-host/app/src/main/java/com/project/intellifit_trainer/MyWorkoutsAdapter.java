package com.project.intellifit_trainer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.AccessController;
import java.util.List;

public class MyWorkoutsAdapter extends RecyclerView.Adapter<MyWorkoutsAdapter.ViewHolder> {
    private List<Workout> workoutList;
    private String userID; // UserID için bir alan

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWorkoutName;
        public LinearLayout llExercisesContainer;
        public Button btnSelectWorkout;


        public ViewHolder(View itemView) {
            super(itemView);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            llExercisesContainer = itemView.findViewById(R.id.llExercisesContainer);
            btnSelectWorkout = itemView.findViewById(R.id.btnSelectWorkout);
        }
    }

    public MyWorkoutsAdapter(List<Workout> workoutList, String userID) {
        this.workoutList = workoutList;
        this.userID = userID; // UserID bilgisini constructor üzerinden al
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Bu satırı düzeltin
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.myworkouts_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.tvWorkoutName.setText(workout.getWorkoutName());
        holder.llExercisesContainer.removeAllViews(); // Önceden eklenmiş view'ları temizle

        for (ExercisesInWorkouts exercise : workout.getExercises()) {
            // Dinamik TextView yarat
            TextView tvExercise = new TextView(holder.itemView.getContext());
            tvExercise.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvExercise.setGravity(Gravity.CENTER);
            tvExercise.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tvExercise.setTextColor(Color.BLACK);
            holder.tvWorkoutName.setTypeface(null, Typeface.BOLD_ITALIC);
            holder.tvWorkoutName.setText(Html.fromHtml("<u>" + holder.tvWorkoutName.getText().toString() + "</u>"));
            tvExercise.setText(String.format("%s - %dx%d", exercise.getName(), exercise.getRepCount(), exercise.getSetCount()));

            // TextView'i LinearLayout'a ekle
            holder.llExercisesContainer.addView(tvExercise);
        }

        updateButton(holder.btnSelectWorkout, workout.isSelected());
        checkIfWorkoutSelected(workout, holder.btnSelectWorkout);


        holder.btnSelectWorkout.setOnClickListener(v -> {
            // Eğer bu workout zaten seçiliyse, seçimini kaldır
            if (workout.isSelected()) {
                workout.setSelected(false);
                unselectWorkout(userID, holder.itemView.getContext());
            } else {
                // Diğer tüm workout'ların seçimlerini kaldır
                for (Workout w : workoutList) {
                    w.setSelected(false);
                }
                // Bu workout'ı seç
                workout.setSelected(true);
                selectWorkout(workout, userID, holder.itemView.getContext());
            }
            // Adapter'ı güncelle
            notifyDataSetChanged();
        });
    }


    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    private void selectWorkout(Workout selectedWorkout, String userID, Context context) {
        if (userID != null && selectedWorkout != null) {
            // Kullanıcı ID'si ve seçilen workout nesnesini kullanarak güncelleme yapın
            DatabaseReference selectedWorkoutRef = FirebaseDatabase.getInstance().getReference("users").child(userID).child("selected_workout");
            selectedWorkoutRef.setValue(selectedWorkout)
                    .addOnSuccessListener(aVoid -> {
                        // Kullanıcı arayüzünde bir geri bildirim ver
                        Log.d("SelectWorkout", "Workout unselected successfully.");
                        Toast.makeText(context, "Workout selected successfully.", Toast.LENGTH_SHORT).show(); // Context kullanıldı
                    })
                    .addOnFailureListener(e -> Log.d("SelectWorkout", "Failed to select workout.", e));
        } else {
            Log.w("SelectWorkout", "User ID or Workout is null");
        }
    }

    private void updateButton(Button button, boolean isSelected) {
        if (isSelected) {
            button.setText("Unselect Workout");
            button.setBackgroundResource(R.drawable.button_remove);
        } else {
            button.setText("Select Workout");
            button.setBackgroundResource(R.drawable.button_add);
        }
    }

    private void unselectWorkout(String userID, Context context) { // Context parametresi eklendi
        DatabaseReference selectedWorkoutRef = FirebaseDatabase.getInstance().getReference("users").child(userID).child("selected_workout");
        selectedWorkoutRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Kullanıcı arayüzünde bir geri bildirim ver
                    Log.d("UnselectWorkout", "Workout unselected successfully.");
                    Toast.makeText(context, "Workout unselected successfully.", Toast.LENGTH_SHORT).show(); // Context kullanıldı
                })
                .addOnFailureListener(e -> {
                    // Hata olması durumunda kullanıcıya bildir
                    Log.w("UnselectWorkout", "Failed to unselect workout.", e);
                    Toast.makeText(context, "Failed to unselect workout.", Toast.LENGTH_SHORT).show(); // Context kullanıldı
                });
    }

    private void checkIfWorkoutSelected(final Workout workout, final Button btnSelectWorkout) {
        DatabaseReference selectedWorkoutRef = FirebaseDatabase.getInstance().getReference("users").child(userID).child("selected_workout");
        selectedWorkoutRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Workout selected = dataSnapshot.getValue(Workout.class);
                    if (selected != null && selected.getWorkoutName().equals(workout.getWorkoutName())) {
                        // Eğer bu workout seçiliyse, butonun durumunu güncelle
                        workout.setSelected(true);
                        updateButton(btnSelectWorkout, true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("checkIfWorkoutSelected", "Error checking selected workout", databaseError.toException());
            }
        });
    }
}

