package com.lhz.sk.himalaya;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lhz.sk.himalaya.adapters.AlbumListAdapter;
import com.lhz.sk.himalaya.bases.BaseActivity;
import com.lhz.sk.himalaya.interfaces.ISearchViewCallBack;
import com.lhz.sk.himalaya.presenters.SearchPresenter;
import com.lhz.sk.himalaya.utils.LogUtil;
import com.lhz.sk.himalaya.views.FlowTextLayout;
import com.lhz.sk.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;

public class SearchActivity extends BaseActivity implements ISearchViewCallBack {

    private ImageView mBackBtn;
    private EditText mSearchEt;
    private TextView mSearchTv;
    private FrameLayout mContentfl;
    private SearchPresenter mSearchPresenter;
    private String TAB = "SearchActivity";
    private FlowTextLayout mTextLayout;
    private UILoader mUiLoader;
    private RecyclerView mSearchlist;
    private AlbumListAdapter mAlbumListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        mSearchPresenter = SearchPresenter.getInstance();
        mSearchPresenter.registerViewCallback(this);
        mSearchPresenter.getHotWord();
    }

    private void initEvent() {
        mBackBtn.setOnClickListener(v -> onBackPressed());
        mSearchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = mSearchEt.getText().toString().trim();
                if (mSearchPresenter != null) {
                    mSearchPresenter.Search(data);
                    mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mUiLoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void OnRetry() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
    }

    private void initView() {
        mBackBtn = findViewById(R.id.back_Iv);
        mSearchEt = findViewById(R.id.search_et);
        mSearchTv = findViewById(R.id.search_tv);
        mContentfl = findViewById(R.id.content_fl);
        //  mTextLayout = findViewById(R.id.flow_text_layout);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup group) {
                    return createSuccess(group);
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            mContentfl.addView(mUiLoader);

        }

    }

    private View createSuccess(ViewGroup group) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.success_search, group, false);
        mSearchlist = inflate.findViewById(R.id.search_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSearchlist.setLayoutManager(linearLayoutManager);
        mAlbumListAdapter = new AlbumListAdapter();
        mSearchlist.setAdapter(mAlbumListAdapter);
        return inflate;
    }

    @Override
    public void onSearchResult(List<Album> albums) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchEt.getWindowToken(),HIDE_IMPLICIT_ONLY);
        LogUtil.e(TAB, albums.size() + "");
        if (albums != null) {
            if (albums.size() == 0) {
                if (mUiLoader != null)
                    mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            } else {
                if (mAlbumListAdapter != null)
                    mAlbumListAdapter.setData(albums);
                if (mUiLoader != null)
                    mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }

    }

    @Override
    public void onHotWordResult(List<HotWord> hotWordList) {
        LogUtil.e(TAB, hotWordList.size() + "");
        List<String> hotwords = new ArrayList<>();
        hotwords.clear();
        for (HotWord hotWord : hotWordList) {

            hotwords.add(hotWord.getSearchword());
        }
//        mTextLayout.setTextContents(hotwords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOk) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> results) {

    }

    @Override
    public void onError(int errCode, String errMsg) {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallback(this);
        }
    }
}