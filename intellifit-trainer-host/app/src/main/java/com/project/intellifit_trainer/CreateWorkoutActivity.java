package com.project.intellifit_trainer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateWorkoutActivity extends AppCompatActivity {
    private RecyclerView rvExercises;
    private WorkoutAdapter adapter;
    private List<WorkoutExercise> exercisesToSave;
    private List<WorkoutExercise> workoutExercisesList;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createworkout);

        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));

        workoutExercisesList = new ArrayList<>();
        workoutExercisesList.add(new WorkoutExercise("Push Up", "Chest, Triceps", R.drawable.push_up_image));
        workoutExercisesList.add(new WorkoutExercise("Dumbbell Curl", "Biceps, Forearms", R.drawable.dumbbell_curl_image));
        workoutExercisesList.add(new WorkoutExercise("High Knees", "Quads, Calves", R.drawable.high_knees_image));
        workoutExercisesList.add(new WorkoutExercise("Cable Triceps", "Triceps, Shoulders", R.drawable.cable_triceps_image));
        workoutExercisesList.add(new WorkoutExercise("Mountain Climbers", "Core, Shoulders", R.drawable.mountain_climbers_image));
        workoutExercisesList.add(new WorkoutExercise("Lunge", "Quads, Glutes", R.drawable.lunge_image));
        workoutExercisesList.add(new WorkoutExercise("Pull Up", "Back, Biceps", R.drawable.pull_up_image));
        workoutExercisesList.add(new WorkoutExercise("Squat", "Quads, Hamstrings", R.drawable.squat_image));
        workoutExercisesList.add(new WorkoutExercise("Jumping Rope", "Calves, Forearms", R.drawable.jumping_rope_image));
        workoutExercisesList.add(new WorkoutExercise("Jumping Jack", "Total Body", R.drawable.jumping_jack_image));

        adapter = new WorkoutAdapter(workoutExercisesList, new WorkoutAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(WorkoutExercise workoutExercise) {
                // İlgili işlem
            }
        });
        rvExercises.setAdapter(adapter);
        // Adapter oluşturulduktan sonra exercisesToSave'i başlat
        exercisesToSave = adapter.getAddedExercises();

        Button btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWorkoutNameDialog();
            }
        });
    }

    private void saveWorkout(String workoutName) {
        if (exercisesToSave.isEmpty()) {
            Toast.makeText(this, "No exercises selected.\nPlease add exercises before saving.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userID).child("workouts");
        String workoutId = databaseRef.push().getKey();

        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("workoutName", workoutName);

        List<Map<String, Object>> exercisesData = new ArrayList<>();
        for (WorkoutExercise exercise : adapter.getAddedExercises()) {
            Map<String, Object> exerciseData = new HashMap<>();
            exerciseData.put("name", exercise.getName());
            exerciseData.put("repCount", exercise.getRepCount());
            exerciseData.put("setCount", exercise.getSetCount());
            exercisesData.add(exerciseData);
        }
        workoutData.put("exercises", exercisesData);

        databaseRef.child(workoutId).setValue(workoutData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Workout saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save workout.", Toast.LENGTH_SHORT).show());

        intent = new Intent(CreateWorkoutActivity.this, CreateWorkoutActivity.class);
        startActivity(intent);
        finish();
    }


    private void showWorkoutNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name Your Workout");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // "Save" butonunu ayarla
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String workoutName = input.getText().toString();
                saveWorkout(workoutName);
            }
        });

        // "Cancel" butonunu ayarla
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
