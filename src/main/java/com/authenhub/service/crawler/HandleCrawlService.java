package com.authenhub.service.crawler;

import com.authenhub.bean.crawl.DataCrawlRequest;
import com.authenhub.bean.crawl.NewsBean;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HandleCrawlService {
    List<NewsBean> getData(DataCrawlRequest wpPublishPostBean) throws IOException;
    CompletableFuture<Object> exportExcel(DataCrawlRequest wpPublishPostBean) throws IOException;
    void importToWp(DataCrawlRequest wpPublishPostBean) throws Exception;
}
