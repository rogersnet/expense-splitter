package com.exsplit;

import java.util.ArrayList;

import com.exsplit.SummaryListAdapter.Group.Type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SummaryListAdapter extends BaseExpandableListAdapter {
	/*
	 * view types
	 */
	private static final int TOTAL_EXP_VIEW = 1;
	private static final int TOTAL_RECV_VIEW = 0;

	/*
	 * data
	 */
	private Context context = null;
	ArrayList<Group> groups = new ArrayList<Group>();

	public SummaryListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).summaryItems.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		int type = -1;
		if (groups.size() == 2 && groupPosition == 1) {
			type = TOTAL_EXP_VIEW;
		} else {
			type = TOTAL_RECV_VIEW;
		}

		return type;
	}

	@Override
	public int getChildTypeCount() {
		return 2;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// get the type of the group this child belongs
		Type viewType = groups.get(groupPosition).type;
		View view = convertView;

		// if the type is future travel, use the future travel layout
		if (viewType == Type.TOTAL_RECV) {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.total_recv_line_item, parent, false);

				TotalRecvViewHolder holder = new TotalRecvViewHolder();
				holder.firstName = (TextView) view
						.findViewById(R.id.tv_firstname02);
				holder.lastName = (TextView) view
						.findViewById(R.id.tv_lastname02);
				holder.amount = (TextView) view
						.findViewById(R.id.tv_total_recv);

				view.setTag(holder);
			}

			TotalRecvViewHolder holder = (TotalRecvViewHolder) view.getTag();

			SummaryItem currentItem = (SummaryItem) getChild(groupPosition,
					childPosition);

			holder.firstName.setText(currentItem.getFirstName());
			holder.lastName.setText(currentItem.getLastName());
			holder.amount.setText(currentItem.getAmount());
		} else {
			// if the type is past, use the past travel layout
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.total_exp_line_item, parent, false);

				TotalExpViewHolder holder = new TotalExpViewHolder();
				holder.firstName = (TextView) view
						.findViewById(R.id.tv_firstname01);
				holder.lastName = (TextView) view
						.findViewById(R.id.tv_lastname01);
				holder.amount = (TextView) view.findViewById(R.id.tv_total_exp);

				view.setTag(holder);
			}

			TotalExpViewHolder holder = (TotalExpViewHolder) view.getTag();

			SummaryItem currentItem = (SummaryItem) getChild(groupPosition,
					childPosition);

			holder.firstName.setText(currentItem.getFirstName());
			holder.lastName.setText(currentItem.getLastName());
			holder.amount.setText(currentItem.getAmount());
		}
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).summaryItems.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		TextView text = null;
		ImageView image = null;

		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.summary_list_group_view, parent, false);
		}

		text = (TextView) view.findViewById(R.id.groupHeader);
		image = (ImageView) view.findViewById(R.id.expandableIcon);

		StringBuilder title = new StringBuilder();
		if (groupPosition == 0) {
			title.append(context.getString(R.string.total_receivables));
		} else {
			title.append(context.getString(R.string.total_expenditure));
		}
		title.append(" (");
		title.append(groups.get(groupPosition).summaryItems.size());
		title.append(")");

		text.setText(title.toString());

		/*
		 * if this is not the first group (future travel) show the arrow image
		 * and change state if necessary
		 */
		if (groupPosition != 0) {
			int imageResourceId = isExpanded ? android.R.drawable.arrow_up_float
					: android.R.drawable.arrow_down_float;
			image.setImageResource(imageResourceId);

			image.setVisibility(View.VISIBLE);
		} else {
			image.setVisibility(View.INVISIBLE);
		}
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void setUpExp(ArrayList<SummaryItem> totalExp,
			ArrayList<SummaryItem> totalRecv) {
		groups.clear();

		if (totalRecv != null) {
			Group g1 = new Group();
			g1.type = Type.TOTAL_RECV;
			g1.summaryItems.clear();
			g1.summaryItems = new ArrayList<SummaryItem>(totalRecv);

			groups.add(g1);
		}
		if (totalExp != null) {
			Group g2 = new Group();
			g2.type = Type.TOTAL_EXP;
			g2.summaryItems.clear();
			g2.summaryItems = new ArrayList<SummaryItem>(totalExp);

			groups.add(g2);
		}

		notifyDataSetChanged();
	}

	/*
	 * Holder for the Past view type
	 */
	class TotalExpViewHolder {
		TextView firstName;
		TextView lastName;
		TextView amount;
	}

	/*
	 * Holder for the Future view type
	 */
	class TotalRecvViewHolder {
		TextView firstName;
		TextView lastName;
		TextView amount;
	}

	/*
	 * Wrapper for each group that contains the list elements and the type of
	 * travel.
	 */
	public static class Group {
		public enum Type {
			TOTAL_EXP, TOTAL_RECV;
		};

		public Type type;
		ArrayList<SummaryItem> summaryItems = new ArrayList<SummaryItem>();
	}
}
