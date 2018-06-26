package com.example.emre.bloodbankcyprus;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {


private FirebaseAuth firebaseAuth;
private FirebaseFirestore firebaseFirestore;


        public List<Comments> commentsList;
        public Context context;

        public CommentsRecyclerAdapter (List<Comments> commentsList){
            this.commentsList=commentsList;
        }


    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            firebaseFirestore = FirebaseFirestore.getInstance();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);

        //it's getting uid but not displayname.
        String commentUserId = commentsList.get(position).getUser_id();
        //commentUser=firebaseAuth.getCurrentUser().getDisplayName();
        //String name = firebaseAuth.getUid();

      //  String comment_name =(firebaseFirestore.collection("Users/" + commentUserId+ "/name")).document(commentUserId).get().toString();


        firebaseFirestore.collection("Users").document(commentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String commentUserName= task.getResult().getString("name");
                    String commentUserProfileImage = task.getResult().getString("image");

                    holder.setComment_user(commentUserName, commentUserProfileImage);

                }
                else
                {
                    //Firebase error
                    Toast.makeText(context,"Firebase error...",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {

        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView commentMessage;
        private TextView commentUserName;
        private CircleImageView commentUserImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setComment_message(String message){
            commentMessage = mView.findViewById(R.id.comment_message);
            commentMessage.setText(message);
        }

        public void setComment_user(String name, String image) {


            commentUserImage = mView.findViewById(R.id.comment_image);
            commentUserName = mView.findViewById(R.id.comment_username);
            commentUserName.setText(name);


            //For glide lib
            RequestOptions placeHolderOption = new RequestOptions();
            placeHolderOption.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeHolderOption).load(image).into(commentUserImage);




        }
    }

}