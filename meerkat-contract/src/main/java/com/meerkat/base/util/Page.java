package com.meerkat.base.util;

import java.io.Serializable;

/**
 * Created by wm on 17/4/9.
 */
public class Page implements Serializable {
    /**
     * @deprecated
     */
    @Deprecated
    private int page = 1;
    private int pageIndex = 1;
    private int pageSize = 10;

    public Page() {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public int getPage() {
        return this.page;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setPage(int page) {
        this.setPageIndex(page);
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        if (pageIndex < 1) {
            pageIndex = 1;
        }

        this.page = pageIndex;
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
