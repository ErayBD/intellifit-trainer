package com.project.intellifit_trainer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class LearnExercisesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ExercisesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learnexercises);

        recyclerView = findViewById(R.id.learnexercises_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Exercise> exercisesList = new ArrayList<>();
        exercisesList.add(new Exercise("Push Up", "https://i.pinimg.com/originals/0e/c1/3b/0ec13bba1befcbf1dc3bedee5dd74af3.gif"));
        exercisesList.add(new Exercise("Dumbbell Curl", "https://i.pinimg.com/originals/ad/d1/05/add105ac87fa75852ca37c13c367102c.gif"));
        exercisesList.add(new Exercise("High Knees", "https://i.pinimg.com/originals/9d/95/8d/9d958df71c059f3fee8257a662b3894c.gif"));
        exercisesList.add(new Exercise("Cable Triceps", "https://global-uploads.webflow.com/5d1d0d3f53ced35a29dbe169/5eb05f51ffcd38185a1d0cad_tricep-pushdown-rope-exercise-anabolic-aliens.gif"));
        exercisesList.add(new Exercise("Mountain Climbers", "https://i.pinimg.com/originals/b6/69/e6/b669e6c36465e76495e9a8cb0cfc7c13.gif"));
        exercisesList.add(new Exercise("Lunge", "https://i.pinimg.com/originals/e8/15/ee/e815ee5095667a9f20b7d8143cfea737.gif"));
        exercisesList.add(new Exercise("Pull Up", "https://assets.menshealth.co.uk/main/assets/assisted.gif?mtime=1456424065"));
        exercisesList.add(new Exercise("Squat", "https://i.pinimg.com/originals/7d/df/b4/7ddfb4d35efd085e9cafce2fd4c3b80e.gif"));
        exercisesList.add(new Exercise("Jumping Rope", "https://i.pinimg.com/originals/54/bd/68/54bd68403893d9eaaad8b1e2d227fd44.gif"));
        exercisesList.add(new Exercise("Jumping Jack", "https://i.pinimg.com/originals/f5/dc/31/f5dc3170295cd59b5afad8fa94429ddb.gif"));

        adapter = new ExercisesAdapter(exercisesList);
        adapter.setOnItemClickListener(new ExercisesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Exercise exercise) {
                showFullSizeImage(exercise);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 50, true));
    }

    private void showFullSizeImage(Exercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LearnExercisesActivity.this);
        builder.setTitle(exercise.getName());

        ImageView imageView = new ImageView(LearnExercisesActivity.this);
        Glide.with(this)
                .load(exercise.getGifUrl())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(imageView);
        builder.setView(imageView);

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

