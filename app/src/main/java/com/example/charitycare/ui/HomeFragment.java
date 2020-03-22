package com.example.charitycare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.charitycare.R;
import com.example.charitycare.activities.PaymentActivity;
import com.example.charitycare.data.Help;
import com.example.charitycare.data.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class HomeFragment extends Fragment {

    private DatabaseReference PostRef;
    private RecyclerView postRecyclerView;
    private FirebaseRecyclerAdapter adapter;

    String username,amount,phonumber;
    LinearLayout card;
    private ViewHolder vHolder;
    LinearLayoutManager linearLayoutManager;
    Help help;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        help = new Help(Objects.requireNonNull(getActivity()));

        postRecyclerView = root.findViewById(R.id.post_recyclerview);
        postRecyclerView.setHasFixedSize(true);

        PostRef = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);




        postRecyclerView.setLayoutManager(linearLayoutManager);

        fetch();

        //displayAllDisabledPosts();


        return root;

    }

    private void fetch() {
        Query query = PostRef.child("Disabled");
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(query, new SnapshotParser<Posts>() {
                    @NonNull
                    @Override
                    public Posts parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Posts(snapshot.child("fullnames").getValue().toString(),
                                snapshot.child("Help").getValue().toString(),
                                "KSH." + snapshot.child("amount").getValue().toString(),
                                snapshot.child("phonenumber").getValue().toString());
                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<Posts, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Posts posts) {

                username = posts.getFullnames();
                amount = posts.getAmount();
                phonumber = posts.getPhonenumber();



                viewHolder.setFullnames(username);
                viewHolder.setAmount(amount);
                viewHolder.setPhonenumber(phonumber);
                viewHolder.setCategory(posts.getHelp());
                //viewHolder.setPhonenumber(posts.getPhonenumber());
                if (getActivity() != null && !help.getIntroDonate()) {
                    MaterialTapTargetPrompt.Builder mttp = new MaterialTapTargetPrompt.Builder(getActivity());
                    mttp.setTarget(viewHolder.txtDonate)
                            .setPrimaryText("Payment")
                            .setSecondaryText("Tap here to open Payment Activity")
                            .show();
                    mttp.setPromptStateChangeListener((prompt, state) -> {
                        if(state == MaterialTapTargetPrompt.STATE_DISMISSED
                        || state ==  MaterialTapTargetPrompt.STATE_FOCAL_PRESSED){
                            new Help(getActivity()).setIntroDonate(true);
                            startActivity(new Intent(getActivity(), PaymentActivity.class));
                        }
                    });
                }

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disable_posts_layout, parent, false);

                return new ViewHolder(view);
            }
        };

        postRecyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MaterialCardView postCard;
        public TextView txtCategory, txtAmount, txtPhone, txtUsername;
        public TextView txtDonate;


        public ViewHolder(View itemView) {
            super(itemView);
            postCard = itemView.findViewById(R.id.post_card);
            txtPhone = itemView.findViewById(R.id.phone);
            txtCategory = itemView.findViewById(R.id.category);
            txtUsername = itemView.findViewById(R.id.username);
            txtAmount = itemView.findViewById(R.id.amount);
            txtDonate = itemView.findViewById(R.id.donate);

            txtDonate.setOnClickListener(this);
        }

        public void setFullnames(String fullnames) {
            txtUsername.setText(fullnames);
        }

        public void setAmount(String amount) {
            txtAmount.setText(amount);
        }

        public void setPhonenumber(String phonenumber) {
            txtPhone.setText(phonenumber);

        }

        public void setCategory(String help) {
            txtCategory.setText(help);
        }

        @Override
        public void onClick(View v)
        {
            Intent paymentIntent = new Intent(getActivity(), PaymentActivity.class);
            paymentIntent.putExtra("username", txtUsername.getText().toString());
            paymentIntent.putExtra("amount", txtAmount.getText().toString());
            paymentIntent.putExtra("phone",txtPhone.getText().toString());
            startActivity(paymentIntent);

        }
    }














  /*  private void displayAllDisabledPosts()
    {
        //reccryler adapter
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                new FirebaseRecyclerOptions.Builder<Posts>()
        Query query =PostRef.child("Disabled");
        FirebaseRecyclerOptions<Posts> options =
                        .setQuery(query,Posts.class)
                        .build();
        FirebaseRecyclerAdapter <Posts,PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                        (options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int i, @NonNull Posts posts)
                    {

                       postsViewHolder.setAmount(posts.getAmount());
                       postsViewHolder.setFullnames(posts.getFullnames());
                       postsViewHolder.setHelp(posts.getHelp());
                       postsViewHolder.setPhonenumber(posts.getPhonenumber());
                    }

                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.disable_posts_layout, parent, false);
                        return new PostsViewHolder(view);
                    }
                };
        postRecyclerView.setAdapter(firebaseRecyclerAdapter);


    }
    public  static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;


        public PostsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;
        }
        public void setFullnames(String fullnames)
        {
            TextView username = (TextView) mView.findViewById(R.id.username);
            username.setText(fullnames);

        }
        public void setAmount(String amount)
        {
            TextView Amount = (TextView) mView.findViewById(R.id.amount);
            Amount.setText(amount);
        }
        public void setPhonenumber(String phonenumber)
        {
            TextView PhoneNumber = (TextView) mView.findViewById(R.id.phone);
            PhoneNumber.setText(phonenumber);

        }
        public void setHelp(String help)
        {
            TextView HelpCategory = (TextView) mView.findViewById(R.id.category);
            HelpCategory.setText(help);
        }
       /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }*/

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}