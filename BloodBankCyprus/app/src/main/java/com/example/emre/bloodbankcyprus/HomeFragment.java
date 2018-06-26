package com.example.emre.bloodbankcyprus;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private RecyclerView bloodseek_list_view;
    private List<SeekPost> seek_list;
    private List<User> user_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private BloodSeekRecyclerAdapter seekRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageLoaded = true;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);

        seek_list = new ArrayList<>();
        user_list = new ArrayList<>();
        bloodseek_list_view = view.findViewById(R.id.bloodseek_list_view);


        firebaseAuth = FirebaseAuth.getInstance();

        seekRecyclerAdapter = new BloodSeekRecyclerAdapter(seek_list, user_list);
        bloodseek_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        bloodseek_list_view.setAdapter(seekRecyclerAdapter);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            bloodseek_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {

                        //String desc = lastVisible.getString("desc");
                        // I Uploaded upto 3rd pic, this is guranteed that i get 3rd image (under toast for checking)
                        //Toast.makeText(container.getContext(), "Reached 3rd one: ", Toast.LENGTH_LONG).show();

                        loadMore();

                    }

                }
            });


            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {
                        if (isFirstPageLoaded) {
                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            seek_list.clear();
                            user_list.clear();
                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String seekPostId = doc.getDocument().getId();
                                final SeekPost seekPost = doc.getDocument().toObject(SeekPost.class).withId(seekPostId);
                                String seekUserId = doc.getDocument().getString("user_id");
                                firebaseFirestore.collection("Users").document(seekUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            User user = task.getResult().toObject(User.class);

                                            if (isFirstPageLoaded) {

                                                seek_list.add(seekPost);
                                                user_list.add(user);

                                            } else {
                                                seek_list.add(0, seekPost);
                                                user_list.add(0, user);
                                            }

                                            seekRecyclerAdapter.notifyDataSetChanged();

                                        }
                                    }
                                });


                            }
                        }

                        isFirstPageLoaded = false;

                    }


                }
            });


        }

        // Inflate the layout for this fragment
        return view;
    }

    public void loadMore() {
        if (firebaseAuth.getCurrentUser() != null) {


                    Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);
//getActivity(),
            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (final DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                final String seekPostId = doc.getDocument().getId();
                                //final SeekPost seekPost = doc.getDocument().toObject(SeekPost.class).withId(seekPostId);
                                String seekUserId = doc.getDocument().getString("user_id");

                                firebaseFirestore.collection("Users").document(seekUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            User user = task.getResult().toObject(User.class);
                                            SeekPost seekPost = doc.getDocument().toObject(SeekPost.class).withId(seekPostId);

                                            seek_list.add(seekPost);
                                            user_list.add(user);


                                            seekRecyclerAdapter.notifyDataSetChanged();

                                        }
                                    }
                                });

                            }
                        }
                    }

                }
            });
        }

    }
}