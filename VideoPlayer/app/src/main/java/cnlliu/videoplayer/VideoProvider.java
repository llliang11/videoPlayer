package cnlliu.videoplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cnlliu on 2015/4/28.
 */
public class VideoProvider {
    private static VideoProvider videoProvider = null;
    private static ContentResolver videoResolver = null;
    private static List<VideoInfo> videoInfos = null;
    private VideoAdapter videoAdapter = null;
    private final static String DEBUG = "video Provider ";

    private static String[] videoColums = {
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.TITLE,
        MediaStore.Video.Media.MIME_TYPE
    };

    public static VideoProvider instance(ContentResolver resolver) {
        if (videoProvider == null) {
            videoResolver = resolver;
            videoProvider = new VideoProvider();
            loadVideoInfo();
        }
        return videoProvider;
    }

    public static void loadVideoInfo() {
        Cursor videoCursor;
        videoInfos = new ArrayList<VideoInfo>();
        videoCursor = videoResolver.query(Media.EXTERNAL_CONTENT_URI, videoColums, null, null, null);
        if (videoCursor.moveToFirst()) {
            do {
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.title = videoCursor.getString(videoCursor.getColumnIndexOrThrow(Media.TITLE));
                videoInfo.filePath = videoCursor.getString(videoCursor.getColumnIndex(Media.DATA));
                videoInfo.mimeType = videoCursor.getString(videoCursor.getColumnIndex(Media.MIME_TYPE));
                videoInfos.add(videoInfo);
            }while (videoCursor.moveToNext());
        }
    }
    public BaseAdapter getAdapter(Context context) {
        videoAdapter = new VideoAdapter(context, videoInfos);
        return videoAdapter;
    }

    public String getVideoUrl(int position) {
        return videoInfos.get(position).filePath;
    }

    static class VideoInfo {
        String filePath;
        String mimeType;
        String title;
    }

    class VideoAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater inflater = null;
        private List<VideoInfo> vInfos = null;

        public VideoAdapter(Context con, List<VideoInfo> vInfo) {
            context = con;
            vInfos = vInfo;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return vInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return vInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vholder = new ViewHolder();
            View videoRow = inflater.inflate(R.layout.video_item, null);
            vholder.position = position;
            vholder.image = (ImageView)videoRow.findViewById(R.id.videoThumbnal);
            vholder.text = (TextView)videoRow.findViewById(R.id.videoTitle);
            vholder.text.setText(vInfos.get(position).title);
            //if ()
            new ThumbnailTask(vholder).execute();
            return videoRow;
        }

        private class ThumbnailTask extends AsyncTask {
            private ViewHolder viewHolder = null;
            private Bitmap bitmap = null;
            ThumbnailTask(ViewHolder vHolder) {
                viewHolder = vHolder;
            }

            @Override
            protected Object doInBackground(Object[] params) {
                bitmap = ThumbnailUtils.createVideoThumbnail(vInfos.get(viewHolder.position).filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap,50,50);
                //System.out.println("do BackGround ...");
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                viewHolder.image.setImageBitmap(bitmap);
            }
        }

        private class ViewHolder {
            ImageView image;
            TextView text;
            int position;
        }
    }
}
