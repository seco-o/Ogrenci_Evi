package com.hk.ogrencievi.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hk.ogrencievi.Activity.IlanDetayActivity;
import com.hk.ogrencievi.Activity.MainActivity;
import com.hk.ogrencievi.Adapter.FavoritesAdapter;
import com.hk.ogrencievi.Adapter.ProductAdapter;
import com.hk.ogrencievi.Helper.PublicHelper;
import com.hk.ogrencievi.Model.Product;
import com.hk.ogrencievi.R;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class FavoriteFragment extends Fragment {
    private LinearLayout bosLinear;
    private CardView ilanlarCard;
    private FirebaseFirestore mFirestore;
    private ArrayList<Product> products;
    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    public FavoriteFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favorites_fragment, container,
                false);
        products = new ArrayList<>();

        bosLinear = view.findViewById(R.id.favorites_boslinear);
        recyclerView = view.findViewById(R.id.favorites_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FavoritesAdapter(products, new FavoritesAdapter.FAListener() {
            @Override
            public void onCardClicked(int position) {
                Intent intent = new Intent(getActivity(), IlanDetayActivity.class);
                intent.putExtra("id",products.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void onCardLongClicked(int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        ilanlarCard = view.findViewById(R.id.favoritesfragment_ilanlar);
        ilanlarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToZero();
            }
        });
        mFirestore = FirebaseFirestore.getInstance();
        getMyFavorites();


        return view;
    }

    private void goToZero() {

        allToNormal(0);
    }
    private void allToNormal(int position) {
        TextView homeText = getActivity().findViewById(R.id.main_hometext);
        ImageView homeIcon = getActivity().findViewById(R.id.main_homeicon);
        if(position != 0){
            homeText.setTextColor(Color.WHITE);
            homeIcon.setImageDrawable(AppCompatResources.getDrawable(getActivity(),R.drawable.bottom_home_icon));
        }
        if(position != 1){
            TextView favoriteText = getActivity().findViewById(R.id.main_favoritetext);
            ImageView favoriteIcon = getActivity().findViewById(R.id.main_favoriteicon);

            favoriteText.setTextColor(Color.WHITE);
            favoriteIcon.setImageDrawable(AppCompatResources.getDrawable(getActivity(),R.drawable.favorite_icon));
        }
        if(position != 2){
            TextView messageText = getActivity().findViewById(R.id.main_messagetext);
            ImageView messageIcon = getActivity().findViewById(R.id.main_messageicon);

            messageText.setTextColor(Color.WHITE);
            messageIcon.setImageDrawable(AppCompatResources.getDrawable(getActivity(),R.drawable.bottom_message_icon));
        }
        if(position != 3){
            TextView profileText = getActivity().findViewById(R.id.main_profiletext);
            ImageView profileIcon = getActivity().findViewById(R.id.main_profileicon);

            profileText.setTextColor(Color.WHITE);
            profileIcon.setImageDrawable(AppCompatResources.getDrawable(getActivity(),R.drawable.bottom_user_icon));
        }

        Fragment fragment = new HomeFragment();
        FragmentManager frgManager = getFragmentManager();
        frgManager.beginTransaction().replace(R.id.main_content_frame, fragment)
                .commit();

        homeText.setTextColor(getActivity().getColor(R.color.color1));
        homeIcon.setImageDrawable(AppCompatResources.getDrawable(getActivity(),R.drawable.bottom_home_icon_selected));

    }

    private void getMyFavorites() {
        String myEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        mFirestore.collection("users").document(Objects.requireNonNull(myEmail))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> data = documentSnapshot.getData();
                        if(data != null){
                            ArrayList<String> favorites = (ArrayList<String>) data.get("favorites");
                            if(favorites == null){
                                favorites = new ArrayList<>();
                            }
                            if(favorites.isEmpty()){
                                isEmpty(); 
                            }else{
                                getProducts(favorites);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        PublicHelper.showError(getActivity(),e.getLocalizedMessage());
                    }
                });
    }

    private void getProducts(ArrayList<String> favorites) {
        bosLinear.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        for(int i=0 ;i<favorites.size();i++){
            int finalI = i;
            mFirestore.collection("products").document(favorites.get(i))
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String,Object> data = documentSnapshot.getData();
                            if(data != null){
                                String id = documentSnapshot.getId();
                                String aciklama = (String) data.get("aciklama");
                                String email = (String) data.get("email");
                                long fiyat = (long) data.get("fiyat");
                                String image = (String) data.get("image");
                                String kategori = (String) data.get("kategori");
                                String okul = (String) data.get("okul");
                                String title = (String) data.get("title");
                                ArrayList<String> images = (ArrayList<String>) data.get("photos");
                                if(images == null){
                                    images = new ArrayList<>();
                                }

                                Product p = new Product(id,aciklama,email,Integer.parseInt(String.valueOf(fiyat)),image,
                                        kategori,okul,title,images);
                                products.add(p);
                            }
                            if(finalI == favorites.size() - 1){
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(finalI == favorites.size() - 1){
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

        }
    }

    private void isEmpty() {
        bosLinear.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }




}
