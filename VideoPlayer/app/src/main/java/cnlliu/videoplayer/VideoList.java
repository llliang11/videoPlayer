package cnlliu.videoplayer;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;


public class VideoList extends ActionBarActivity {
    private ListView videoList = null;
    private BaseAdapter videoAdapter = null;
    private VideoProvider videoProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        videoList = (ListView)findViewById(R.id.videoList);
        System.out.println("arrvire here ...video list");
        videoProvider = VideoProvider.instance(this.getContentResolver());
        videoAdapter = videoProvider.getAdapter(this);
        videoList.setAdapter(videoAdapter);

        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String videoUrl = videoProvider.getVideoUrl(position);
                jumpToVideoPlay(videoUrl);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void jumpToVideoPlay(String url) {
        Intent intent = new Intent(VideoList.this, VideoPlay.class);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
