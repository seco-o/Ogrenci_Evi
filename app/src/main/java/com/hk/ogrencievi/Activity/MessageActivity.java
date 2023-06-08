package com.hk.ogrencievi.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;
import com.hk.ogrencievi.Adapter.MesajlasmaAdapter;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Model.Message;
import com.hk.ogrencievi.Model.ProdUser;
import com.hk.ogrencievi.Model.User;
import com.hk.ogrencievi.Public.Constants;
import com.hk.ogrencievi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity {
    private String userEmail;
    private ArrayList<Message> messages;
    private Activity mActivity;
    private MesajlasmaAdapter adapter;
    private Toolbar toolbar;
    private ImageView userImage;
    private RecyclerView recyclerView;
    private EditText chatEdit;
    private CardView fab;
    private ProdUser user;
    private String myId;
    private FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        userEmail = getIntent().getStringExtra("email");

        bindIDs();

        getMyId();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMyScreen(userEmail);

    }

    private void updateMyScreen(String screen) {
        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        mFirestore.collection("users").document(Objects.requireNonNull(myEmail))
                .update("screen",screen);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateMyScreen("");
    }

    private void getMyId() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data != null){
                            myId = (String) data.get("uuid");
                            getUser();
                        }else{
                            Toast.makeText(mActivity, "Beklenmeyen hata", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mActivity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.FIREBASE_URL).getReference("messages")
                .child(myId).
                child(user.getUuid());




        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String,Object> data =  (Map<String, Object>) snapshot.getValue();
                if(data != null){
                    String id = (String) data.get("id");
                    String senderEmail = (String) data.get("senderEmail");
                    String text = (String) data.get("text");
                    Map<String,Object> timeMap = (Map<String, Object>) data.get("time");
                    Date time = new Date();
                    time.setTime((long)timeMap.get("time"));
                    Message m = new Message(id,text,senderEmail,time);
                    messages.add(m);

                }
                sortMessage();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sortMessage() {
        messages.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                if(o1.getTime().after(o2.getTime())){
                    return 1;
                }else if(o2.getTime().after(o1.getTime())){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void bindIDs() {
        messages = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        mActivity = this;
        toolbar = findViewById(R.id.activity_message_toolbar);
        setSupportActionBar(toolbar);
        userImage = findViewById(R.id.activity_message_image);
        recyclerView = findViewById(R.id.activity_message_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new MesajlasmaAdapter(messages);
        recyclerView.setAdapter(adapter);

        chatEdit = findViewById(R.id.activity_message_edit);
        fab = findViewById(R.id.activity_message_send);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction();
            }
        });


        chatEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                    sendAction();
                    return true;
                }
                return false;
            }
        });


        
    }

    private void sendAction() {
        String chatString = chatEdit.getText().toString().trim();
        if(!chatString.isEmpty() && myId != null){
            String id = UUID.randomUUID().toString();
            Message message = new Message(id,chatString,
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),
                    new Date(System.currentTimeMillis()));

            DatabaseReference myReferance = FirebaseDatabase.getInstance(Constants.FIREBASE_URL).getReference("messages")
                    .child(myId).child(user.getUuid()).child(id);

            DatabaseReference userReferance = FirebaseDatabase.getInstance(Constants.FIREBASE_URL).getReference("messages")
                    .child(user.getUuid()).child(myId).child(id);

            myReferance.setValue(message);
            userReferance.setValue(message);
            chatEdit.setText("");
            if(user.getOneSignal() != null){
                sendPush(user.getOneSignal());
            }

        }else{
            Toast.makeText(mActivity, "Mesaj giriniz", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPush(String oneSignal) {
        mFirestore.collection("users").document(userEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String screen = documentSnapshot.getString("screen");
                        if(screen != null){
                            String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            if(!screen.equals(myEmail)){
                                ArrayList<String> oneSignalArray = new ArrayList<>();
                                oneSignalArray.add(oneSignal);
                                PublicHelper.sendPush("Yeni mesajınız var",oneSignalArray);
                            }
                        }else{
                            ArrayList<String> oneSignalArray = new ArrayList<>();
                            oneSignalArray.add(oneSignal);
                            PublicHelper.sendPush("Yeni mesajınız var",oneSignalArray);
                        }
                    }
                });
    }

    private void getUser() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users").document(userEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data != null){
                            String ad = (String) data.get("ad");
                            String documentUrl = (String) data.get("documentUrl");
                            String email = (String) data.get("email");
                            String okulName = (String) data.get("okulName");
                            boolean onay = (boolean) data.get("onay");
                            String pp = (String) data.get("pp");
                            String soyad = (String) data.get("soyad");
                            String telNo = (String) data.get("telNo");
                            String uuid = (String) data.get("uuid");
                            String oneSignal = (String) data.get("oneSignal");

                            user = new ProdUser(ad,documentUrl,email,okulName,
                                    onay,pp,soyad,telNo,uuid,oneSignal);

                            setUserProperties();
                            getData();
                        }else{
                            Toast.makeText(mActivity, "Beklenmeyen Hata", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        PublicHelper.showError(mActivity,e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setUserProperties() {
        String toolbarTitle = user.getAd() + " "+
                user.getSoyad();
        toolbar.setTitle(toolbarTitle);
        Picasso.get().load(Uri.parse(user.getPp())).into(userImage);
    }


}