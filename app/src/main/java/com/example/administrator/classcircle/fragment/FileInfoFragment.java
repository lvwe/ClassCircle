package com.example.administrator.classcircle.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.administrator.classcircle.utils.ShowFileUtils;
import com.example.administrator.classcircle.utils.AnimationUtils;
import com.example.administrator.classcircle.entity.FileInfo;
import com.example.administrator.classcircle.adapter.FileInfoAdapter;
import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.utils.ToastUtils;
import com.example.administrator.classcircle.activity.ChooseFileActivity;
import com.example.administrator.classcircle.Application;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class FileInfoFragment extends Fragment {
    private static final String TAG = "FileInfoFragment";

    private int mType = FileInfo.TYPE_APK;
    private List<FileInfo> mFileInfoList;
    private FileInfoAdapter mFileInfoAdapter;

    private GridView mGridView;
    private ProgressBar mProgressBar;

    @SuppressLint("ValidFragment")
    public FileInfoFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public FileInfoFragment(int type) {
        super();
        this.mType = type;

    }

    public static FileInfoFragment newInstance(int type) {
        FileInfoFragment fragment = new FileInfoFragment(type);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper =
                new ContextThemeWrapper(getActivity(), R.style.AppTheme_NoActionBar);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View rootView = localInflater.inflate(R.layout.fragment_file_info, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.grid_view);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        if (mType == FileInfo.TYPE_APK) {
            mGridView.setNumColumns(4);
        } else if (mType == FileInfo.TYPE_JPG) {
            mGridView.setNumColumns(3);
        } else if (mType == FileInfo.TYPE_MP3) {
            mGridView.setNumColumns(1);
        } else if (mType == FileInfo.TYPE_MP4) {
            mGridView.setNumColumns(1);
        }
        init();
        return rootView;
    }

    private void init() {
        if (mType == FileInfo.TYPE_APK) {
            new GetFileInfoListTask(getContext(), FileInfo.TYPE_APK).executeOnExecutor(Application.MAIN_EXECUTOR);
        } else if (mType == FileInfo.TYPE_JPG) {
            new GetFileInfoListTask(getContext(), FileInfo.TYPE_JPG).executeOnExecutor(Application.MAIN_EXECUTOR);
        } else if (mType == FileInfo.TYPE_MP3) {
            new GetFileInfoListTask(getContext(), FileInfo.TYPE_MP3).executeOnExecutor(Application.MAIN_EXECUTOR);
        } else if (mType == FileInfo.TYPE_MP4) {
            new GetFileInfoListTask(getContext(), FileInfo.TYPE_MP4).executeOnExecutor(Application.MAIN_EXECUTOR);
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileInfo = mFileInfoList.get(position);
                if (Application.getAppContent().isExist(fileInfo)) {
                    Application.getAppContent().deleteFileInfo(fileInfo);
                    updateSelectedView();
                } else {
                    Application.getAppContent().addFileInfo(fileInfo);
                    View startView;
                    View targetView = null;
                    startView = view.findViewById(R.id.iv_shortcut);
                    // instanceof 用来在运行时指出对象是否是特定类的一个实例
                    if (getActivity() != null && (getActivity() instanceof ChooseFileActivity)) {
                        ChooseFileActivity mainActivity = (ChooseFileActivity) getActivity();
                        targetView = mainActivity.getSelectView();
                    }
                    AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);
                }
                mFileInfoAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * 更新FileInfoAdapter
     */
    public void updateFileInfoAdapter() {
        if (mFileInfoAdapter != null) {
            mFileInfoAdapter.notifyDataSetChanged();
        }
    }

    private void updateSelectedView() {
        if (getActivity() != null && (getActivity() instanceof ChooseFileActivity)) {
            ChooseFileActivity mainActivity = (ChooseFileActivity) getActivity();
            mainActivity.getSelectView();
        }
    }

    // 实现异步任务机制   Handle  AsyncTask

    /**
     * Handler模式需要为每一个任务创建一个新的线程，任务完成后通过Handler实例向UI线程发送消息，
     * 完成界面的更新，这种方式对于整个过程的控制比较精细，
     * 但也是有缺点的，例如代码相对臃肿，在多个任务同时执行时，不易对线程进行精确的控制
     * <p>
     * <p>
     * 三种泛型类型分别代表“启动任务执行的输入参数”、“后台任务执行的进度”、“后台计算结果的类型
     * <p>
     * 1.execute(Params... params)，执行一个异步任务，需要我们在代码中调用此方法，触发异步任务的执行。
     * 2.onPreExecute()，在execute(Params... params)被调用后立即执行，一般用来在执行后台任务前对UI做一些标记。
     * 3.doInBackground(Params... params)，在onPreExecute()完成后立即执行，用于执行较为费时的操作，此方法将接收输入参数和返回计算结果。
     * 在执行过程中可以调用publishProgress(Progress... values)来更新进度信息。
     * 4.onProgressUpdate(Progress... values)，在调用publishProgress(Progress... values)时，此方法被执行，直接将进度信息更新到UI组件上。
     * 5.onPostExecute(Result result)，当后台操作结束时，此方法将会被调用，计算结果将做为参数传递到此方法中，直接将结果显示到UI组件上。
     * 在使用的时候，有几点需要格外注意：
     * 1.异步任务的实例必须在UI线程中创建。
     * 2.execute(Params... params)方法必须在UI线程中调用。
     * 3.不要手动调用onPreExecute()，doInBackground(Params... params)，onProgressUpdate(Progress... values)，
     * onPostExecute(Result result)这几个方法。
     * 4.不能在doInBackground(Params... params)中更改UI组件的信息。
     * 5.一个任务实例只能执行一次，如果执行第二次将会抛出异常。
     */
    class GetFileInfoListTask extends AsyncTask<String, Integer, List<FileInfo>> {

        Context sContext = null;
        int sType = FileInfo.TYPE_APK;
        List<FileInfo> sFileInfoList = null;

        public GetFileInfoListTask(Context context, int sType) {
            sContext = context;
            this.sType = sType;
        }

        /**
         * 被调用后立即执行，一般用来在执行后台任务前对UI做一些标记。
         */
        @Override
        protected void onPreExecute() {
            showProgressBar();
            super.onPreExecute();
        }

        /**
         * 在onPreExecute()完成后立即执行，用于执行较为费时的操作，此方法将接收输入参数和返回计算结果。
         * 在执行过程中可以调用publishProgress(Progress... values)来更新进度信息。
         * @param params
         * @return
         */
        @Override
        protected List<FileInfo> doInBackground(String... params) {
            if (sType == FileInfo.TYPE_APK) {
                sFileInfoList = ShowFileUtils.getSpecificTypeFiles(sContext, new String[]{FileInfo.EXTEND_APK});
                sFileInfoList = ShowFileUtils.getDetailFileInfo(sContext, sFileInfoList, FileInfo.TYPE_APK);
            } else if (sType == FileInfo.TYPE_JPG) {
                sFileInfoList = ShowFileUtils.getSpecificTypeFiles(sContext, new String[]{FileInfo.EXTEND_JPG});
                sFileInfoList = ShowFileUtils.getDetailFileInfo(sContext, sFileInfoList, FileInfo.TYPE_JPG);
            } else if (sType == FileInfo.TYPE_MP3) {
                sFileInfoList = ShowFileUtils.getSpecificTypeFiles(sContext, new String[]{FileInfo.EXTEND_MP3});
                sFileInfoList = ShowFileUtils.getDetailFileInfo(sContext, sFileInfoList, FileInfo.TYPE_MP3);
            } else if (sType == FileInfo.TYPE_MP4) {
                sFileInfoList = ShowFileUtils.getSpecificTypeFiles(sContext, new String[]{FileInfo.EXTEND_MP4});
                sFileInfoList = ShowFileUtils.getDetailFileInfo(sContext, sFileInfoList, FileInfo.TYPE_MP4);
            }
            mFileInfoList = sFileInfoList;
            return sFileInfoList;
        }


        /**
         * 在调用publishProgress(Progress... values)时，此方法被执行，直接将进度信息更新到UI组件上。
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /**
         * 当后台操作结束时，此方法将会被调用，计算结果将做为参数传递到此方法中，直接将结果显示到UI组件上。
         *
         * @param fileInfos
         */
        @Override
        protected void onPostExecute(List<FileInfo> fileInfos) {
            hideProgressBar();
            if (sFileInfoList != null && sFileInfoList.size() > 0) {
                if (mType == FileInfo.TYPE_APK) {
                    mFileInfoAdapter = new FileInfoAdapter(sContext, sFileInfoList, FileInfo.TYPE_APK);
                    mGridView.setAdapter(mFileInfoAdapter);
                } else if (mType == FileInfo.TYPE_JPG) {
                    mFileInfoAdapter = new FileInfoAdapter(sContext, sFileInfoList, FileInfo.TYPE_JPG);
                    mGridView.setAdapter(mFileInfoAdapter);
                } else if (mType == FileInfo.TYPE_MP3) {
                    mFileInfoAdapter = new FileInfoAdapter(sContext, sFileInfoList, FileInfo.TYPE_MP3);
                    mGridView.setAdapter(mFileInfoAdapter);
                } else if (mType == FileInfo.TYPE_MP4) {
                    mFileInfoAdapter = new FileInfoAdapter(sContext, sFileInfoList, FileInfo.TYPE_MP4);
                    mGridView.setAdapter(mFileInfoAdapter);
                } else {
                    ToastUtils.show(sContext, "没有找到apk");
                }
            }
        }
    }

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null && mProgressBar.isShown()) {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
