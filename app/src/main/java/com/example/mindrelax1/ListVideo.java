package com.example.mindrelax1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ListVideo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        FirebaseStorage.getInstance().getReference().child("videos").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                ArrayList<Video> arrayList = new ArrayList<>();
                VideoAdapter adapter = new VideoAdapter(ListVideo.this, arrayList);
                adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Video video) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getUrl()));
                        intent.setDataAndType(Uri.parse(video.getUrl()), "video/mp4");
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);
                listResult.getItems().forEach(new Consumer<StorageReference>() {
                    @Override
                    public void accept(StorageReference storageReference) {
                        Video video = new Video();
                        video.setTitle(storageReference.getName());
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String url = "https://" + task.getResult().getEncodedAuthority() + task.getResult().getEncodedPath() + "?alt=media&token=" + task.getResult().getQueryParameters("token").get(0);
                                video.setUrl(url);
                                arrayList.add(video);
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ListVideo.this, "Failed to retrieve videos", Toast.LENGTH_SHORT).show();
            }
        });



    }
}