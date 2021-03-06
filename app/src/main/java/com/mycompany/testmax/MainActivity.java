package com.mycompany.testmax;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    public static final String filename="Questions.xls"; //Name of the excel file
    public static Map<String, List<String>> allAnswer=new HashMap<>();
    public static Map<String, String> correctAns=new HashMap<>();
    public static Map<String, List<String>> categories=new TreeMap<>();
    public static Map<String, String> questions=new HashMap<>();
    public static Set<String> unanswered=new HashSet<>();
    public static Map<String, List<String>> review=new HashMap<>();
    String correctAll,correct1,correct2;
    public static String[] check={"Argument Structure Questions","Main Point Questions"};
    public static int[] scores={0,0,0};
    public static int[] attempt={0,0,0};
    public static boolean read=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(read)
            readFile();
        unanswered=new HashSet<>(allAnswer.keySet());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CardView cardView=findViewById(R.id.cardAll);
        cardView.setRadius(50);
        cardView=findViewById(R.id.cardArg);
        cardView.setRadius(50);
        cardView=findViewById(R.id.cardMain);
        cardView.setRadius(50);
        rePaint();
    }

    public void review(View view){
        String val="";
        switch (view.getId()){
            case R.id.button:
                val="All";
                break;
            case R.id.button1:
                val="Argument Structure Questions";
                break;
            case R.id.button2:
                val="Main Point Questions";
                break;
        }
        if(review.size()==0 || (!val.equals("All") && !review.containsKey(val))) {
            Toast.makeText(this, "No questions to review in this section", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent(MainActivity.this, ReviewActivity.class);
        intent.putExtra("Category",val);
        startActivity(intent);
    }

    public void goNext(View view){
        Intent intent=new Intent(MainActivity.this, DisplayActivity.class);
        String val="";
        int idx=0,size=0;
        switch (view.getId()){
            case R.id.cardAll:
                val="All";
                idx=0;
                break;
            case R.id.cardArg:
                val="Argument";
                idx=1;
                break;
            case R.id.cardMain:
                val="Main";
                idx=2;
                break;
        }
        if(idx==0)
            size=categories.get(check[0]).size()+categories.get(check[1]).size();
        else
            size=categories.get(check[idx-1]).size();
        if(attempt[idx]==size){
            Toast.makeText(this, "You have answered all questions here!"+"\n"+"Try another section", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("Category",val);
        intent.putExtra("Idx",idx);
        startActivity(intent);
    }

    public void resetAll(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Practice again?");
        alertDialogBuilder.setMessage("All the previous responses will be reset."+"\n"+"Are you sure?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                unanswered=new HashSet<>(allAnswer.keySet());
                coordinatorLayout=findViewById(R.id.coordinator);
                Arrays.fill(scores,0);
                Arrays.fill(attempt,0);
                review.clear();
                rePaint();
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }


    public void readFile(){         //Reads the excel file and populates the data to maps
        try{
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(filename);
            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            int cols = sheet.getColumns();
            String id="";
            System.out.print(rows+" "+cols+"\n");
            for(int i=2; i < rows; i++){
                List<String> temp=new ArrayList<>();
                String cat="",correct="",quest="";
                for(int j=0; j < cols; j++){
                    Cell cell = sheet.getCell(j,i);
                    String value = cell.getContents();
                    if(value.equals(""))
                        break;
                    if (j == 0)
                        id = value; //set.add(value);
                    else if(j==1)
                        quest=value;
                    else if (j == 2)
                        cat=value;
                    else if (j > 2)
                        temp.add(value);
                    System.out.println(value);
                    if(cell.getCellFormat()!=null) {
                        Colour colour = cell.getCellFormat().getBackgroundColour();
//                        System.out.println("inside the if");
//                        System.out.println(colour.getDescription());
//                        System.out.println(colour == Colour.BLUE);
                        if(colour.getDescription().equals("yellow"))
                            correct=value;
                        //BLUE = question // YELLOW = answer
                    }
                }
                allAnswer.put(id, temp);
                List<String> a=new ArrayList<>();//=categories.getOrDefault(cat,new ArrayList<String>());
                if(categories.containsKey(cat))
                    a=categories.get(cat);
                a.add(id);
                categories.put(cat, a);
                questions.put(id, quest);
                correctAns.put(id, correct);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        System.out.println(allAnswer);
        System.out.println(categories);
        System.out.println(correctAns);
        System.out.println(correctAns);
        System.out.println(allAnswer.size()+" "+categories.size()+" "+correctAns.size()+" "+questions.size());
        read=false;
    }

    public void rePaint(){
        TextView score1=findViewById(R.id.progress1);
        TextView score11=findViewById(R.id.progress11);
        correctAll=scores[0]+"/";
        score1.setText(correctAll+questions.size());
        correctAll=attempt[0]+"/";
        score11.setText(correctAll+questions.size());

        TextView score2=findViewById(R.id.progress2);
        TextView score22=findViewById(R.id.progress22);
        TextView category1=findViewById(R.id.category2);
        correct1=scores[1]+"/";
        score2.setText(correct1+""+categories.get(category1.getText()).size());
        correct1=attempt[1]+"/";
        score22.setText(correct1+""+categories.get(category1.getText()).size());

        TextView score3=findViewById(R.id.progress3);
        TextView score33=findViewById(R.id.progress33);
        TextView category2=findViewById(R.id.category3);
        correct2=scores[2]+"/";
        score3.setText(correct2+""+categories.get(category2.getText()).size());
        correct2=attempt[2]+"/";
        score33.setText(correct2+""+categories.get(category2.getText()).size());
    }

    @Override
    public void onResume(){
        super.onResume();
        rePaint();
    }
}
