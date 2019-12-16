package tr.edu.mu.ceng.gui.mytimeline;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ListenerRegistration listenerRegistration;
    List<Post> posts = new ArrayList<>();
    Button btnPost;
    public static final String TAG = "Timeline";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        listenerRegistration = db.collection("notes").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Error retrieving notes", e);
                    return;
                }
                posts.clear();
                posts.addAll(queryDocumentSnapshots.toObjects(Post.class));
                ListView listView = (ListView) findViewById(R.id.listView);
                PostAdapter adapter = new PostAdapter(MainActivity.this, posts);
                listView.setAdapter(adapter);
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Post post = new Post();
        post.setMessage(data.getCharSequenceArrayExtra("msg").toString());
        Bitmap image = data.getParcelableExtra("bitmap");

        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("posts").document();

        post.setImagePath("images/"+ docRef.getId());
        post.setDate(new Timestamp(new Date()));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference(post.getImagePath());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(bytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                docRef.set(post);
            }
        });
        btnPost = findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PostActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
        //super.onStop();
    }
}
