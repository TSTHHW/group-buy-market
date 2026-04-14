package cn.bugstack.domain.tag.service;

public interface ITagService {
    // 执行人群标签批量任务
    void execTagBatchJob(String tagId, String batchId);
}
