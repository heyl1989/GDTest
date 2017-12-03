package com.pf.gdtest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.pf.gdtest.R;
import com.pf.gdtest.util.AMapUtil;

import java.util.ArrayList;
import java.util.List;

public class PoiKeyworkSearchTestActivity extends AppCompatActivity implements TextWatcher, Inputtips.InputtipsListener, AdapterView.OnItemClickListener {

    private AutoCompleteTextView searchText;// 输入搜索关键字
    private EditText editCity;// 要输入的城市名字或者城市区号
    private ListView listView;
    private MAdapter mAdapter;
    private List<Tip> tipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_keywork_search_test);
        init();
    }

    private void init() {
        searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
        // 添加文本输入框监听事件
        searchText.addTextChangedListener(this);
        editCity = (EditText) findViewById(R.id.city);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);

        tipList = new ArrayList<>();
        mAdapter = new MAdapter(tipList);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, editCity.getText().toString());
            Inputtips inputTips = new Inputtips(PoiKeyworkSearchTestActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onGetInputtips(List<Tip> tipLists, int rCode) {
        // 正确返回
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            this.tipList.clear();
            this.tipList.addAll(tipLists);
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, rCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Tip tip = (Tip) adapterView.getAdapter().getItem(position);
        Toast.makeText(this, tip.getName(), Toast.LENGTH_SHORT).show();
    }

    private class MAdapter extends BaseAdapter {

        private List<Tip> mTipList;

        public MAdapter(List<Tip> tipList) {
            this.mTipList = tipList;
        }

        @Override
        public int getCount() {
            return mTipList.size();
        }

        @Override
        public Object getItem(int i) {
            return mTipList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_inputs, null, false);
                view.setTag(new ViewHolder(view));
            }
            initData(position, (ViewHolder) view.getTag());
            return view;
        }

        private void initData(int position, ViewHolder holder) {
            Tip tip = mTipList.get(position);
            holder.online_user_list_item_textview.setText(
                    tip.getName() + "\n"
                    + tip.getAddress() + "\n"
                    + (tip.getPoint() == null ? "经纬度为空" : tip.getPoint().toString()) + "\n"
                    + tip.getAdcode() + "\n"
                    + tip.getDistrict() + "\n"
                    + tip.getPoiID() + "\n"
                    + tip.getTypeCode());
        }

        private class ViewHolder {
            private TextView online_user_list_item_textview;

            public ViewHolder(View view) {
                online_user_list_item_textview = (TextView) view.findViewById(R.id.online_user_list_item_textview);
            }
        }
    }
}