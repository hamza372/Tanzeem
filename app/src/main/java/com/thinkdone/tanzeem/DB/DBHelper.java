package com.thinkdone.tanzeem.DB;

import android.provider.BaseColumns;

public class DBHelper {

    public static class CategoryEntry implements BaseColumns {
        public static final String ID = "ID";
        public static final String NAME_ENG = "NameEng";
        public static final String NAME_URDU = "NameUrdu";
        public static final String SORT_ORDER = "SortOrder";
        public static final String TABLE_NAME = "category";
    }

    public static class AuthorEntry implements BaseColumns {
        public static final String ID = "ID";
        public static final String NAME_ENG = "NameEng";
        public static final String NAME_URDU = "NameUrdu";
        public static final String DETAILS = "Details";
        public static final String TABLE_NAME = "authors";
    }

    public static class KitabEntry implements BaseColumns {
        public static final String ID = "ID";
        public static final String NAME_ENG = "NameEng";
        public static final String NAME_URDU = "NameUrdu";
        public static final String PUBLISHER_NAME = "PublisherName";
        public static final String PUBLISHER_DETAILS = "PublisherDetails";
        public static final String NAME = "Name";
        public static final String PUBLISHER_NAME_URDU = "PublisherNameUrdu";
        public static final String PUBLISHER_DETAILS_URDU = "PublisherDetailsUrdu";
        public static final String PUBLISHER_VERSION = "PublishVersion";
        public static final String IMAGE_NAME = "ImageName";
        public static final String CATEGORY_ID = "CategoryID";
        public static final String TABLE_NAME = "books";
        public static final String IS_ACTIVE = "IsActive";
    }

    public static class PageEntry implements BaseColumns {
        public static final String ID = "ID";
        public static final String BAAB_ID = "BaabID";
        public static final String DESC_URDU = "DescUrdu";
        public static final String DETAILS = "Detail";
        public static final String DETAILS_WEB = "DetailWeb";
        public static final String DETAILS_WEB_TRANSLATION = "DetailWebTranslation";
        public static final String PAGE_NO = "PageNo";
        public static final String NAME_ENG = "TitleEng";
        public static final String NAME_URDU = "TitleUrdu";
        public static final String IS_IMAGE = "IsImage";
        public static final String IMAGE_NAME = "ImageName";
        public static final String TABLE_NAME = "pages";
    }

    public static class BaabEntry implements BaseColumns {
        public static final String ID = "ID";
        public static final String Baab_ID = "BaabID";
        public static final String NAME = "Name";
        public static final String BOOK_ID = "BookID";
        public static final String TABLE_NAME = "abwaab";
    }
    public static class BookmarkEntry implements BaseColumns {
        public static final String TABLE_NAME = "saved_pages";
        public static final String KITAB_ID = "KITAB_ID";
    }
}
