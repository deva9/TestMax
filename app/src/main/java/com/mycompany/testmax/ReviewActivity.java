package com.mycompany.testmax;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    String categoryIntent,value,key,child;
    List<String> listOfQuestions,allQuestions,answers;
    int i;
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Intent intent=getIntent();
        categoryIntent=intent.getStringExtra("Category");
        allQuestions=new ArrayList<>();
        listOfQuestions=new ArrayList<>();
        for(String str: MainActivity.review.keySet()){
            List<String> temp=MainActivity.review.get(str);
            allQuestions.addAll(temp);
            if(str.equals(categoryIntent)){
                listOfQuestions=new ArrayList<>(temp);
                value=str;
            }
        }
        scrollView=findViewById(R.id.scroller);
        i=0;
        if(categoryIntent.equals("All"))
            listOfQuestions=new ArrayList<>(allQuestions);
        again();
        setCategory();
    }

    public void again(){
        if(listOfQuestions.size()==i){
            alert();
            return;
        }
        scrollView.smoothScrollTo(0,0);
        CardView card=findViewById(R.id.cardAll);
        card.setRadius(60);
        ViewGroup viewGroup=(ViewGroup)card.getChildAt(0);
        for(int i=1;i<viewGroup.getChildCount();i++){
            CardView temp=(CardView) viewGroup.getChildAt(i);
            temp.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        String breaker=listOfQuestions.get(i);
        String[] strs=breaker.split("\\s");
        key=strs[1];
        child=strs[0];
        answers=new ArrayList<>(MainActivity.allAnswer.get(key));
        setCategory();
        populateQuestion(key);
        paint();
    }

    public void paint(){
        int correct,wrong;
        if(child.charAt(0)=='-') {
            wrong=-1;
            correct=child.charAt(2)-'0';
        }
        else{
            correct=child.charAt(0)-'0';
            wrong=child.charAt(1)-'0';
        }
        CardView card=findViewById(R.id.cardAll);
        ViewGroup viewGroup=(ViewGroup)card.getChildAt(0);
        CardView cardView1=(CardView) viewGroup.getChildAt(correct);
        cardView1.setBackgroundColor(Color.parseColor("#66b032"));
        if(wrong!=-1) {
            cardView1=(CardView) viewGroup.getChildAt(wrong);
            cardView1.setBackgroundColor(Color.parseColor("#fe2612"));
        }
    }

    public void next(View view){
        if(i==listOfQuestions.size()){
            alert();
            return;
        }
        i++;
        again();
    }

    public void populateQuestion(String key){
        scrollView.smoothScrollTo(0,0);
        TextView textView1=findViewById(R.id.question);
        String setter="<p>"+MainActivity.questions.get(key)+"</p>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView1.setText(Html.fromHtml(setter, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView1.setText(Html.fromHtml(setter));
        }
        textView1=findViewById(R.id.Answert1);
        textView1.setText(answers.get(0));
        textView1=findViewById(R.id.Answert2);
        textView1.setText(answers.get(1));
        textView1=findViewById(R.id.Answert3);
        textView1.setText(answers.get(2));
        textView1=findViewById(R.id.Answert4);
        textView1.setText(answers.get(3));
        textView1=findViewById(R.id.Answert5);
        textView1.setText(answers.get(4));
    }

    public void setCategory(){
        TextView textView=findViewById(R.id.categoryValue);
        textView.setText(getCategory());
    }

    public String getCategory(){
        if(MainActivity.review.keySet().contains(categoryIntent))
            return categoryIntent;
        for(String s: MainActivity.categories.keySet()) {
            if (key==null && s.equals(categoryIntent))
                return s;
            if (MainActivity.categories.get(s).contains(key))
                return s;
        }
        return categoryIntent;
    }

    public void alert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Finished");
        alertDialogBuilder.setMessage("No more questions left to review in this section ");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        alertDialogBuilder.show();
    }
    @Override
    public void onBackPressed() { super.onBackPressed(); }

}
