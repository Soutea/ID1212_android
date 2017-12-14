package se.souri.tea.dicegenerator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements DiceRollListener {
    private TextView diceRollView;
    private String diceRollTemplate;
    private ReceiverThread receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        diceRollView = (TextView)findViewById(R.id.diceRollView);
        diceRollTemplate = getResources().getString(R.string.dice_roll_result);
        receiver = new ReceiverThread(this);
        receiver.setDaemon(true);
        receiver.start();

    }

    @Override
    public void onDiceRoll(final byte roll) {
        runOnUiThread(new Runnable() { // behövs för att isolera trådar
            @Override
            public void run() {
                diceRollView.setText(String.format(diceRollTemplate, roll));
            }
        });
        // OM KOTLIN ELLER JAVA 8: runOnUiThread(() -> diceRollView.blahblahblah);
    }

    public void rollAgain(View v){
        receiver.rollDiceAgain();
    }
}
