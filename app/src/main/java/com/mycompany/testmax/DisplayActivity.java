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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    List<String> section,all,answers;
    String key,categoryIntent,value,d;
    int idx;
    boolean clicked=false;
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent=getIntent();
        categoryIntent=intent.getStringExtra("Category");
        idx=intent.getIntExtra("Idx",-1);
        System.out.println(idx);
        all=new ArrayList<>();
        section=new ArrayList<>();
        answers=new ArrayList<>();
        for(String s: MainActivity.categories.keySet()){
            List<String> temp=MainActivity.categories.get(s);
            all.addAll(temp);
            if(s.contains(categoryIntent)) {
                section=new ArrayList<>(temp);
                value=s;
            }
        }
        scrollView=findViewById(R.id.scroller);
        d=(section.isEmpty())?"All":value;
        if(section.isEmpty())
            section=new ArrayList<>(all);
        List<String> temp=new ArrayList<>();
        for(String str: section){
            if(MainActivity.unanswered.contains(str))
                temp.add(str);
        }
        System.out.println("Display: "+MainActivity.unanswered.size());
        section=new ArrayList<>(temp);
        System.out.println("After: "+section+"\n\n\n");
        setCategory();
        again();
    }

    public void again(){
//        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.smoothScrollTo(0,0);
        if(section.isEmpty()) {
            clicked=true;
            alert(1);
            return;
        }
        CardView card=findViewById(R.id.cardAll);
        card.setRadius(60);
        Collections.shuffle(section);
        key=section.get(0);
        answers=new ArrayList<>(MainActivity.allAnswer.get(key));
        ViewGroup viewGroup=(ViewGroup)card.getChildAt(0);
        for(int i=1;i<viewGroup.getChildCount();i++){
            CardView temp=(CardView) viewGroup.getChildAt(i);
            temp.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        setCategory();
        populateQuestion(key);
    }

    public void checkAnswer(View view){
        if(clicked)
            return;
        CardView card=findViewById(R.id.cardAll);
        ViewGroup viewGroup=(ViewGroup)card.getChildAt(0);
        int correctIndex=-1,wrongIndex=-1;
        CardView cardView=findViewById(view.getId());
        TextView text=((TextView)cardView.getChildAt(0));
        String ans=text.getText().toString();
        String correctAnswer=MainActivity.correctAns.get(key);
        wrongIndex = ((LinearLayout) cardView.getParent()).indexOfChild(cardView);
        if(correctAnswer.equals(ans)) {
            cardView.setBackgroundColor(Color.parseColor("#66b032"));
            updateScores(true,idx);
        }
        else {
            cardView.setBackgroundColor(Color.parseColor("#fe2612"));
            for(int i=1;i<viewGroup.getChildCount();i++){
                CardView temp=(CardView) viewGroup.getChildAt(i);
                TextView answerText=((TextView)temp.getChildAt(0));
                if(answerText.getText().toString().equals(correctAnswer)) {
                    temp.setBackgroundColor(Color.parseColor("#66b032"));
                    correctIndex=i;
                    break;
                }
            }
            updateScores(false,idx);
        }
        String v=""+correctIndex+wrongIndex+" "+key;
        if(MainActivity.review.containsKey(getCategory())){
            List<String> temp=MainActivity.review.get(getCategory());
            temp.add(v);
        }
        else
            MainActivity.review.put(getCategory(),new ArrayList<String>(Arrays.asList(v)));
        System.out.println(correctIndex+"\n"+wrongIndex);
        clicked=true;
    }

    public void updateScores(boolean flag, int idx) {
        int count = -1;
        MainActivity.attempt[idx]++;
        if (idx != 0)
            MainActivity.attempt[0]++;
        else {
            count = 1;
            for (String s : MainActivity.categories.keySet()) {
                if (s.equals(getCategory()))
                    break;
                count++;
            }
            MainActivity.attempt[count]++;
        }
        if(flag){
            MainActivity.scores[idx]++;
            if (idx!=0)
                MainActivity.scores[0]++;
            else {
                MainActivity.scores[count]++;
            }
        }
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

    public void next(View view){
        if(section.isEmpty()){
            alert(2);
            return;
        }
        if(clicked){
            section.remove(0);
            MainActivity.unanswered.remove(key);
        }
        else{
            String rem=section.remove(0);
            section.add(rem);
        }
        clicked=false;
        again();
    }

    public void setCategory(){
        TextView textView=findViewById(R.id.categoryValue);
        textView.setText(getCategory());
    }

    public String getCategory(){
        for(String s: MainActivity.categories.keySet()) {
            if (key==null && s.contains(categoryIntent))
                return s;
            if (MainActivity.categories.get(s).contains(key))
                return s;
        }
        return categoryIntent;
    }

    public void alert(int x){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Finished");
        alertDialogBuilder.setMessage("You have finished all questions in this section ");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBuilder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with discard
//                Toast.makeText(AlertDialogActivity.this, "Discard", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
        alertDialogBuilder.show();

    }
}
