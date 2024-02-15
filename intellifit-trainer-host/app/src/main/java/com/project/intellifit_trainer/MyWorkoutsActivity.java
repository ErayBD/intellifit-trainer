package com.project.intellifit_trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWorkoutsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyWorkoutsAdapter adapter;
    private List<Workout> workoutList;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myworkouts);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid(); // Mevcut kullanıcının ID bilgisini al
        }

        recyclerView = findViewById(R.id.rvMyWorkouts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        workoutList = new ArrayList<>();
        adapter = new MyWorkoutsAdapter(workoutList, userID);
        recyclerView.setAdapter(adapter);

        loadWorkouts();
    }

    private void loadWorkouts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userID).child("workouts");
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    workoutList.clear();
                    for (DataSnapshot workoutSnapshot : dataSnapshot.getChildren()) {
                        Workout workout = new Workout();
                        workout.setWorkoutName(workoutSnapshot.child("workoutName").getValue(String.class));
                        // Alıştırmaları bir liste olarak alın
                        List<ExercisesInWorkouts> exercisesList = new ArrayList<>();
                        for (DataSnapshot exerciseSnapshot : workoutSnapshot.child("exercises").getChildren()) {
                            ExercisesInWorkouts exercise = exerciseSnapshot.getValue(ExercisesInWorkouts.class);
                            exercisesList.add(exercise);
                        }
                        workout.setExercises(exercisesList);
                        workoutList.add(workout);
                    }
                    adapter.notifyDataSetChanged();
                    adapter = new MyWorkoutsAdapter(workoutList, userID);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Hata durumunda yapılacak işlemler
                }
            });
        }
    }
}
