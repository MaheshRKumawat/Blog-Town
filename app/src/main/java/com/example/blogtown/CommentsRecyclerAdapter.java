package com.example.blogtown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public CommentsRecyclerAdapter(List<Comments> commentsList){
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String commentMessage = commentsList.get(position).getMessage();
        String user_id= commentsList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.setUserData(userName, userImage);
                }else{

                }
            }
        });
        holder.setComment_message(commentMessage);
    }

    @Override
    public int getItemCount() {
        if(commentsList!=null){
            return commentsList.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView comment_message;
        private TextView commentUserName;
        private ImageView commentUserImage;

        public ViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message){
            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }

        public void setUserData(String name, String image){
            commentUserImage = mView.findViewById(R.id.comment_image);
            commentUserName = mView.findViewById(R.id.comment_username);
            commentUserName.setText(name);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.blog_town_profile_photo);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(commentUserImage);
        }

    }
}
