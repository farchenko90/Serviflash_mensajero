package fabio.org.serviflash_mensajero.Util;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.TimePicker;

/**
 * Created by Gilmar Ocampo Nieves on 02/05/2016.
 */
public class TimePicker2 extends TimePickerDialog {

    public static int TIME_PICKER_INTERVAL=1;
    private boolean mIgnoreEvent=false;
    private EditText txtHora;

    public TimePicker2(Context context, OnTimeSetListener callBack, int hourOfDay, int minute,
                       boolean is24HourView, EditText txtHora) {
        super(context, callBack, hourOfDay, minute, is24HourView);
        this.txtHora = txtHora;
    }
    /*
     * (non-Javadoc)
     * @see android.app.TimePickerDialog#onTimeChanged(android.widget.TimePicker, int, int)
     * Implements Time Change Interval
     */
    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        super.onTimeChanged(timePicker, hourOfDay, minute);
        //this.setTitle("2. Select Time");
        if (!mIgnoreEvent){
            minute = getRoundedMinute(minute);
            mIgnoreEvent=true;
            timePicker.setCurrentMinute(minute);
            mIgnoreEvent=false;
            String hora = String.valueOf(hourOfDay);
            String minu = String.valueOf(minute);
            if(hourOfDay < 10)
                hora = "0"+hora;
            if(minute < 10)
                minu = "0"+minu;
            txtHora.setText(hora + ":" + minu + ":00" );
        }
    }

    public String setHoraActual(int hourOfDay, int minute){
        minute = getRoundedMinute(minute);
        String hora = String.valueOf(hourOfDay);
        String minu = String.valueOf(minute);
        if(hourOfDay < 10)
            hora = "0"+hora;
        if(minute < 10)
            minu = "0"+minu;
        return hora + ":" + minu + ":00" ;
    }

    public static int getRoundedMinute(int minute){
        try {
            if (minute % TIME_PICKER_INTERVAL != 0) {
                int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
                minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
                if (minute == 60) minute = 0;
            }
            return minute;
        }catch(Exception e){

        }
        return 0;
    }
}
