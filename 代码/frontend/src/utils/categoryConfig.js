export const categories = [
    { value: 1, label: '电子书籍', icon: 'el-icon-notebook-1' },
    { value: 2, label: '软件工具', icon: 'el-icon-cpu' },
    { value: 3, label: '课程学习', icon: 'el-icon-video-play' },
    { value: 4, label: '设计模版', icon: 'el-icon-brush' },
    { value: 5, label: '其他', icon: 'el-icon-more' },
    { value: 10, label: '公告展示', icon: 'el-icon-bell' }
];

// 辅助方法：根据ID获取名称
export function getCategoryLabel(value) {
    const item = categories.find(c => c.value === parseInt(value));
    return item ? item.label : '未知分类';
}

// 辅助方法：获取标签样式类型 (可选，用于美化Tag)
export function getCategoryTagType(value) {
    const types = {
        '1': 'primary',
        '2': 'success',
        '3': 'warning',
        '4': 'info',
        '5': 'primary',
        '10': 'danger'
    };
    return types[value.toString()] || 'info';
}