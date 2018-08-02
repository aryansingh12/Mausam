package com.example.aryansingh.weatherontherocks.pojo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aryansingh.weatherontherocks.Datum;
import com.example.aryansingh.weatherontherocks.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Aryan Singh on 7/27/2018.
 */

public class DailyWeatherAdaptor extends RecyclerView.Adapter<DailyWeatherAdaptor.WeatherViewHolder>{

    public Context context;
    public List<Datum> list;
    public MoviesClickListener listener;
    Calendar calendar = Calendar.getInstance();

    public DailyWeatherAdaptor(Context context, List<Datum> list, MoviesClickListener listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.daily_row_layout,parent,false);
        return new WeatherViewHolder(itemView,listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
//
        Datum datum = list.get(position);
        calendar.setTimeInMillis(datum.getTime());

        if(datum.getTime()!=null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(datum.getTime());
            holder.time.setText(calendar.get(Calendar.HOUR) + "");

//            holder.time.setText();
//            calendar.setTimeInMillis(datum.getTime());
        }
        if(datum.getTemperatureMin()!=null){
            holder.low_temperature.setText(convertToC(datum.getTemperatureMin()) + " \u2103");
        }
        if(datum.getTemperatureMax()!=null){
            holder.high_temperature.setText(convertToC(datum.getTemperatureMax()) + " \u2103");
        }
        if(datum.getHumidity()!=null){
            holder.humidity.setText(datum.getHumidity()*100 + "%");
        }
        //Picasso.with(context).load(R.drawable.sun).into(holder.image);
        holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.sun));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface MoviesClickListener {
        void onMovieClick(View view, int position);
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        TextView time,high_temperature,low_temperature,humidity;
        MoviesClickListener moviesClickListener;

        public WeatherViewHolder(View itemView, MoviesClickListener listener) {
            super(itemView);
            moviesClickListener = listener;
            humidity = itemView.findViewById( R.id.humidity);
            time = itemView.findViewById(R.id.time);
            high_temperature = itemView.findViewById(R.id.high_temperature);
            low_temperature = itemView.findViewById(R.id.low_temperature);
            image = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            moviesClickListener.onMovieClick(view,getAdapterPosition());
        }
    }

    public String convertToC(double temperature){
        temperature = ((temperature - 32)*5)/9;
        String s = temperature + "";
        s = s.substring(0,2);
        return s;
    }
}
