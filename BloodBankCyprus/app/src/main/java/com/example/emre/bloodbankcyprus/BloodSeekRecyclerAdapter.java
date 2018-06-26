package com.example.emre.bloodbankcyprus;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BloodSeekRecyclerAdapter extends RecyclerView.Adapter<BloodSeekRecyclerAdapter.ViewHolder>{

    public List<SeekPost> seek_list;
    public List<User> user_list;

    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public BloodSeekRecyclerAdapter(List<SeekPost> seek_list, List<User> user_list){

        this.seek_list=seek_list;
        this.user_list=user_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bloodseek_list_item, parent, false);


                context=parent.getContext();
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseAuth=FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String seekPostId = seek_list.get(position).SeekPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        String image_url = seek_list.get(position).getImage_url();

        String thumbUri = seek_list.get(position).getImage_thumb();
        holder.setSeekImage(image_url,thumbUri);

        String desc_data  = seek_list.get(position).getDesc();
        holder.setDescView(desc_data);

        String bloodtype_data = seek_list.get(position).getBlood_type();
        holder.setBtypeView(bloodtype_data);

        String location_data = seek_list.get(position).getLocation();
        holder.setLocationView(location_data);

            holder.setSeekImage(image_url, thumbUri);

            String seek_user_id = seek_list.get(position).getUser_id();

            if (seek_user_id.equals(currentUserId)) {


                    holder.foundButton.setEnabled(true);
                    holder.foundButton.setVisibility(View.VISIBLE);
            }



                    String userName =user_list.get(position).getName();
                    String userProfileImage = user_list.get(position).getImage();
                    holder.setUserData(userName, userProfileImage);




       try {
           long millisecond = seek_list.get(position).getTimestamp().getTime();

                String dateString = DateFormat.format("MM/dd/yyyy",new Date(millisecond)).toString();
                holder.setTime(dateString);
       }catch (Exception e) {
           Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
       }



       //Comments Counter ..
        firebaseFirestore.collection("Posts/" + seekPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(documentSnapshots != null && !documentSnapshots.isEmpty()){
                    //if not empty means no ups..
                    int count_comment= documentSnapshots.size();
                    holder.updateCommentsCount(count_comment);
                    holder.seekCommentBtn.setImageDrawable(context.getDrawable(R.mipmap.comment_main_red));

                }
                else {

                    holder.updateUpsCount(0);
                    holder.seekCommentBtn.setImageDrawable(context.getDrawable(R.mipmap.comment_main));

                }
            }
        });







            //Ups Counter ..
        firebaseFirestore.collection("Posts/" + seekPostId + "/Ups").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(documentSnapshots!=null && !documentSnapshots.isEmpty()){
                    //if not empty means no ups..
                    int count = documentSnapshots.size();
                holder.updateUpsCount(count);
                }
                else {

                    holder.updateUpsCount(0);
                }
            }
        });


       //Check ups states if there is exist change icon according that.

            firebaseFirestore.collection("Posts/" + seekPostId + "/Ups").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot!=null && documentSnapshot.exists()){

                    holder.seekUpBtn.setImageDrawable(context.getDrawable(R.mipmap.add_new_up_red_filled));

                }else  {
                    holder.seekUpBtn.setImageDrawable(context.getDrawable(R.mipmap.add_new_up_red));

                }


            }
        });


            //Up - (This is kind of facebook likes or eksisozluk up button there is backend of that feature
        holder.seekUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + seekPostId + "/Ups").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()){
                            Map<String, Object> upsMap = new HashMap<>();
                            upsMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + seekPostId + "/Ups").document(currentUserId).set(upsMap);
                        }
                        else {
                            firebaseFirestore.collection("Posts/" + seekPostId + "/Ups").document(currentUserId).delete();
                        }




                    }
                });




            }
        });


    holder.foundButton.setOnClickListener(new View.OnClickListener() {

    /*
        firebase official delete post method! ???

      db.collection("Posts").document("DC")
        .delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
        */
        @Override
        public void onClick(View v) {
            firebaseFirestore.collection("Posts").document(seekPostId).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            seek_list.remove(position);
                            user_list.remove(position);
                            Toast.makeText(context,"Data is deleted! Thanks for attention!",Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context,"Data cannot delete! Please try again..!",Toast.LENGTH_SHORT).show();


                        }
                    });

        }
    });

        holder.seekCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);

                commentIntent.putExtra("seek_post_id",seekPostId);
                context.startActivity(commentIntent);

            }
        });



        //   Toast.makeText(context,"Data Value Is: " + seekPostId,Toast.LENGTH_SHORT).show();

    /*          There is only for testing seekPostId which is the post id of posts is coming there or not!
    Sure it's coming!!!!

       DocumentReference docRef = firebaseFirestore.collection("Posts").document(seekPostId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            Toast.makeText(context,"DocumentSnapshot data: " + document.getData(),Toast.LENGTH_SHORT).show();

                        } else {
                         //   Log.d(TAG, "No such document");
                            Toast.makeText(context,"No such document",Toast.LENGTH_SHORT).show();

                        }
                    } else {
                   //     Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(context,"get failed with",Toast.LENGTH_SHORT).show();

                    }
                }
            });*/

    }

    @Override
    public int getItemCount() {
        return seek_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView descView;
        private TextView btypeView;
        private TextView locationView;
        private ImageView seekImageView;
        private TextView seekDate;

        private TextView seekUserName;
        private CircleImageView seekUserImage;

        private ImageView seekUpBtn;
        private TextView seekUpCount;

        private Button foundButton;

        private ImageView seekCommentBtn;
        private TextView seekComments;


        public ViewHolder(View itemView) {
            super(itemView);

              mView = itemView;
                seekUpBtn = mView.findViewById(R.id.seek_up_btn);
                seekCommentBtn = mView.findViewById(R.id.seek_comment_btn);

                foundButton=mView.findViewById(R.id.seek_found_btn);

        }

            public void setDescView(String descText){

            descView = mView.findViewById(R.id.seek_desc);
            descView.setText(descText);

            }
            public void setBtypeView(String btypeText ){

            btypeView = mView.findViewById(R.id.seek_blood_type);
            btypeView.setText(btypeText);
        }
        public void setLocationView(String locationText) {

            locationView = mView.findViewById(R.id.seek_blood_location);
            locationView.setText(locationText);

        }

            public void setSeekImage(String downloadUri, String thumbUri){

                seekImageView =  mView.findViewById(R.id.seek_image);


                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.image_placeholder);


                Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                        Glide.with(context).load(thumbUri))
                        .into(seekImageView);



            }
            public void setTime(String date) {


            seekDate=mView.findViewById(R.id.seek_date);
            seekDate.setText(date);
            }

            public void setUserData(String name, String image){


            seekUserImage = mView.findViewById(R.id.comment_image);
            seekUserName = mView.findViewById(R.id.comment_username);

            seekUserName.setText(name);


            //For glide lib
                RequestOptions placeHolderOption = new RequestOptions();
                placeHolderOption.placeholder(R.drawable.profile_placeholder);



            Glide.with(context).applyDefaultRequestOptions(placeHolderOption).load(image).into(seekUserImage);

            }

            public void updateUpsCount(int count){
            seekUpCount=mView.findViewById(R.id.seek_up_count);
            seekUpCount.setText(count + "Up's");
            }
        public void updateCommentsCount(int count_comment){
            seekComments=mView.findViewById(R.id.seek_comments_count);
            seekComments.setText(count_comment + "Comments");
        }


    }


}

