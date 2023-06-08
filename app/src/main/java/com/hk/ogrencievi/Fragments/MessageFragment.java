package com.hk.ogrencievi.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;
import com.hk.ogrencievi.Activity.MessageActivity;
import com.hk.ogrencievi.Adapter.MesajlarAdapter;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Model.MesajData;
import com.hk.ogrencievi.Model.MesajlarItem;
import com.hk.ogrencievi.Model.MessageAndTime;
import com.hk.ogrencievi.Public.Constants;
import com.hk.ogrencievi.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class MessageFragment extends Fragment {
    private ArrayList<MesajlarItem> items;
    private MesajlarAdapter adapter;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    public MessageFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.messages_fragment, container,
                false);

        items = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.messages_fragment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MesajlarAdapter(items,
                new MesajlarAdapter.MAListener() {
                    @Override
                    public void onCardClicked(int position) {
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("email",items.get(position).getEmail());
                        startActivity(intent);

                    }
                });
        recyclerView.setAdapter(adapter);

        getMyId();


        return view;
    }

    private void getMyId() {
        String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        mFirestore.collection("users").document(myEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data != null){
                            String id = (String) data.get("uuid");
                            getMessages(id);
                        }else{
                            Toast.makeText(getActivity(), "Beklenmeyen hata", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getMessages(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.FIREBASE_URL).
                getReference("messages").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MesajData> mesajDatas = new ArrayList<>();
                ArrayList<DataSnapshot> datas = new ArrayList<>();
                items.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot children : dataSnapshot.getChildren()){
                        datas.add(children);
                    }
                    MesajData mesajData = new MesajData(dataSnapshot.getKey(),datas);
                    mesajDatas.add(mesajData);
                }
                getAllMessage(mesajDatas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAllMessage(ArrayList<MesajData> datas){
        for(int i=0; i<datas.size(); i++){
            MesajData mesajData = datas.get(i);

            int finalI = i;
            int finalI1 = i;
            mFirestore.collection("users").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                                        Map<String,Object> userData = (Map<String, Object>) ds.getData();
                                        if(userData != null){
                                            String id = (String) userData.get("uuid");
                                            System.out.println("id: "+id);
                                            System.out.println("userid: "+mesajData.getUserId());
                                            if(id.equals(mesajData.getUserId())){
                                                String ad = (String) userData.get("ad");
                                                String soyad = (String) userData.get("soyad");
                                                String pp = (String) userData.get("pp");
                                                String email = (String) userData.get("email");

                                                System.out.println(mesajData.getUserId());
                                                ArrayList<MessageAndTime> dates = new ArrayList<>();
                                                for(int j=0; j<mesajData.getDatas().size(); j++){
                                                    Map<String,Object> map = (Map<String, Object>) mesajData.getDatas().get(j).getValue();
                                                    Map<String,Object> timeMap = (Map<String, Object>) map.get("time");
                                                    String text = (String) map.get("text");
                                                    System.out.println(map);
                                                    MessageAndTime date = new MessageAndTime(text,new Date((long) timeMap.get("time")));
                                                    dates.add(date);
                                                }
                                                dates.sort(new Comparator<MessageAndTime>() {
                                                    @Override
                                                    public int compare(MessageAndTime o1, MessageAndTime o2) {
                                                        if(o1.getTime().after(o2.getTime())){
                                                            return -1;
                                                        }
                                                        else if(o2.getTime().after(o1.getTime())){
                                                            return 1;
                                                        }
                                                        else{
                                                            return 0;
                                                        }
                                                    }
                                                });

                                                MesajlarItem item = new MesajlarItem(id,ad+" "+soyad,pp,dates.get(finalI1).getMessage(),dates.get(0).getTime(),
                                                        email);
                                                items.add(item);
                                            }
                                        }
                                    }

                                    if(finalI == datas.size() - 1){
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });


        }
    }


}
