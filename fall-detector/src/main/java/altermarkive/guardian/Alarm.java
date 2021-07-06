package altermarkive.guardian;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.widget.Toast;

public class Alarm {
    private static SoundPool pool = null;
    private static int id = -1;

    @SuppressWarnings("deprecation")
    public static void siren(Context context) {
        if (null == pool) {
            pool = new SoundPool(5, AudioManager.STREAM_ALARM, 0);
        }
        if (-1 == id) {
            id = pool.load(context.getApplicationContext(), R.raw.alarm, 1);
        }
        loudest(context, AudioManager.STREAM_ALARM);
        pool.play(id, 1.0f, 1.0f, 1, 3, 1.0f);
    }

    public static void loudest(Context context, int stream) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int loudest = manager.getStreamMaxVolume(stream);
        manager.setStreamVolume(stream, loudest, 0);
    }

    public static void call(Context context) {
        String contact = Contact.get(context);
        if (contact != null && !"".equals(contact)) {
            Toast.makeText(context, "Alerting the emergency phone number", Toast.LENGTH_SHORT).show();
            Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact));
            call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(call);
            Telephony.handsfree(context);
        } else {
            Toast.makeText(context, "ERROR: Emergency phone number not set", Toast.LENGTH_SHORT).show();
            siren(context);
        }
    }
}
