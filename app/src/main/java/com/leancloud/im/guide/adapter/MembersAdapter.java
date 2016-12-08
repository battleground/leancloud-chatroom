package com.leancloud.im.guide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abooc.joker.adapter.recyclerview.BaseRecyclerAdapter;
import com.abooc.joker.adapter.recyclerview.ViewHolder;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.leancloud.im.guide.R;
import com.leancloud.im.guide.adapter.MembersAdapter.MemberItem;
import com.leancloud.im.guide.viewholder.MemberHolder;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by wli on 15/8/14.
 * 成员列表 Adapter
 */
public class MembersAdapter extends BaseRecyclerAdapter<MemberItem> {

    /**
     * 在有序 memberList 中 MemberItem.sortContent 第一次出现时的字母与位置的 map
     */
    private Map<Character, Integer> indexMap = new HashMap<Character, Integer>();

    /**
     * 简体中文的 Collator
     */
    Collator cmp = Collator.getInstance(Locale.SIMPLIFIED_CHINESE);

    public MembersAdapter(Context context) {
        super(context);
    }

    /**
     * 设置成员列表，然后更新索引
     * 此处会对数据以 空格、数字、字母（汉字转化为拼音后的字母） 的顺序进行重新排列
     */
    public void setMemberList(List<String> list) {
        getCollection().clear();
        if (null != list) {
            Observable.from(list)
                    .map(new Func1<String, MemberItem>() {
                        @Override
                        public MemberItem call(String s) {
                            MemberItem item = new MemberItem();
                            item.content = s;
                            item.sortContent = PinyinHelper.convertToPinyinString(s, "", PinyinFormat.WITHOUT_TONE);
                            return item;
                        }
                    })
                    .subscribe(new Action1<MemberItem>() {
                        @Override
                        public void call(MemberItem memberItem) {
                            getCollection().add(memberItem);
                        }
                    });
        }
        Collections.sort(getCollection(), new SortChineseName());
        updateIndex();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_member_item, parent, false);
        return new MemberHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        MemberItem member = getItem(position);
        if (member == null) return;
        MemberHolder holder = (MemberHolder) h;
        holder.bindData(member);
    }

    /**
     * 获取索引 Map
     */
    public Map<Character, Integer> getIndexMap() {
        return indexMap;
    }

    /**
     * 更新索引 Map
     */
    private void updateIndex() {
        Character lastCharcter = '#';
        indexMap.clear();
        for (int i = 0; i < getItemCount(); i++) {
            Character curChar = Character.toLowerCase(getItem(i).sortContent.charAt(0));
            if (!lastCharcter.equals(curChar)) {
                indexMap.put(curChar, i);
            }
            lastCharcter = curChar;
        }
    }

    public class SortChineseName implements Comparator<MemberItem> {

        @Override
        public int compare(MemberItem str1, MemberItem str2) {
            if (null == str1) {
                return -1;
            }
            if (null == str2) {
                return 1;
            }
            if (cmp.compare(str1.sortContent, str2.sortContent) > 0) {
                return 1;
            } else if (cmp.compare(str1.sortContent, str2.sortContent) < 0) {
                return -1;
            }
            return 0;
        }
    }

    public static class MemberItem {
        public String content;
        public String sortContent;
    }
}