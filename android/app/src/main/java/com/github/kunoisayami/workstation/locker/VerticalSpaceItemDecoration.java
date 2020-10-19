package com.github.kunoisayami.workstation.locker;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// https://stackoverflow.com/a/27037230
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
	private final int verticalSpaceHeight;
	private static final String TAG = "log_VerticalSpaceItem";

	public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
		this.verticalSpaceHeight = verticalSpaceHeight;
	}

	@Override
	public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
							   @NonNull RecyclerView.State state) {
		if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
			outRect.bottom = verticalSpaceHeight;
		}
	}
}