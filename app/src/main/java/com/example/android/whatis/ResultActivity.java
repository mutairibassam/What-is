package com.example.android.whatis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends BottomSheetDialogFragment {

    private static final String TAG = "ResultActivity";

    private TextView searchResult;
    private String userInput;

    private Button btnBack, btnNotification, btnNotInterested;

    private boolean checkAvailability;

    private LottieAnimationView lottieAnimationView;


    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_result, container, false);

        if (getArguments() != null) {
            userInput = getArguments().getString("key");
            Log.d(TAG, "onCreateView: " + userInput);
        }

        lottieAnimationView = view.findViewById(R.id.animation);
        lottieAnimationView.setVisibility(View.VISIBLE);

        searchResult = view.findViewById(R.id.tv_result);
        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setVisibility(View.INVISIBLE);

        btnNotInterested = view.findViewById(R.id.btn_not_interested);
        btnNotInterested.setVisibility(View.INVISIBLE);

        btnNotification = view.findViewById(R.id.btn_notification);
        btnNotification.setVisibility(View.INVISIBLE);

        firebaseFirestore.collection("keywords")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String reformatted = documentSnapshot.getId();
                                if (reformatted.equals(userInput)) {
                                    String value = documentSnapshot.getData().toString();
                                    lottieAnimationView.setVisibility(View.INVISIBLE);
                                    btnBack.setVisibility(View.VISIBLE);
                                    searchResult.setText(value);
                                    checkAvailability = true;
                                    break;
                                } else {
                                    checkAvailability = false;
                                }
                            }
                            if (checkAvailability == false) {
                                lottieAnimationView.setVisibility(View.INVISIBLE);
                                btnNotification.setVisibility(View.VISIBLE);
                                btnNotInterested.setVisibility(View.VISIBLE);
                                String message = "Not found, Please notify me to be added.";
                                searchResult.setText(message);

                            }

                        } else {
                            Log.w(TAG, "Error getting documents", task.getException());
                        }
                    }
                });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToHome();
            }
        });

        btnNotInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 backToHome();
            }
        });

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("missing")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        String reformatted = documentSnapshot.getId();
                                        if (reformatted.equals(userInput)) {
                                            Toast.makeText(getActivity(), "Thanks, it will be added soon", Toast.LENGTH_LONG).show();
                                            break;
                                        } else {
                                            Map<String, Object> missing = new HashMap<>();
                                            missing.put("Empty", "Empty");
                                            firebaseFirestore.collection("missing").document(userInput)
                                                    .set(missing)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getActivity(), "Thanks, it will be added soon", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                                            startActivity(intent);

                                                        }
                                                    })

                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Something went wrong! please try again", Toast.LENGTH_LONG).show();
                                                            Log.w(TAG, "Error writing document", e);
                                                        }
                                                    });
                                        }
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Something went wrong! please try again", Toast.LENGTH_LONG).show();

                                }
                            }
                        });

            }

        });

        return view;

    }

    public void backToHome() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

}
