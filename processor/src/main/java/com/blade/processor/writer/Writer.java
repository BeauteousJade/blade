package com.blade.processor.writer;

import com.blade.processor.entry.ClassEntry;

import java.util.List;

public interface Writer {

    void writer(List<ClassEntry> classEntryList);
}
