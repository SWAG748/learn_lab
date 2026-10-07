package com.learnlab.common;

import java.util.List;

/** Spring Data 的分页从 0 开始，这里原样传给前端，页面上再换成从 1 开始的页码。 */
public record PageResult<T>(List<T> items, int page, int size, long total, int totalPages) {
}
