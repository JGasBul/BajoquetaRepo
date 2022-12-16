package com.example.bajoquetaapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class recipesAdapter extends FirestoreRecyclerAdapter<recipesData, recipesAdapter.RecipesViewHolder> {

    private final Context context;
    protected View.OnClickListener onClickListener;

    public recipesAdapter(Context context, @NonNull FirestoreRecyclerOptions<recipesData> options) {
        super(options);
        this.context = context.getApplicationContext();
    }

    public class RecipesViewHolder extends RecyclerView.ViewHolder {

        public final ImageView imageView;
        public final TextView mtitle, mDescription;

        public RecipesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.tvImage);
            this.mtitle = itemView.findViewById(R.id.tvTitle);
            this.mDescription = itemView.findViewById(R.id.tvDescription);
        }
    }

    @NonNull
    @Override
    public RecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipes_row_item, parent, false);
        return new recipesAdapter.RecipesViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipesViewHolder holder, int position, @NonNull recipesData model) {
        holder.mtitle.setText(model.getNombre());
        holder.mDescription.setText(model.getDescripcion());
        Glide.with(context).load(model.getFoto()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RecipesDetails.class);
                intent.putExtra("calorias",model.getCalorias());
                intent.putExtra("description", model.getDescripcion());
                intent.putExtra("foto",model.getFoto());
                intent.putExtra("grasas",model.getGrasas());
                intent.putExtra("hidratos",model.getHidratos());
                intent.putExtra("ingredientes",model.getIngredientes());
                intent.putExtra("name",model.getNombre());
                intent.putExtra("persona",model.getPersonas());
                intent.putExtra("proteinas",model.getProteinas());
                intent.putExtra("tiempo",model.getTiempo());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }
}

