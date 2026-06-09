# 秘典 GUI 渲染审计

源码参考：`../eidolon-1.20x` 使用 1.19.2 GUI/渲染栈、`RenderType`、
`MultiBufferSource` 和 core shader JSON。Legacy 目标是 Minecraft 1.12.2 /
Cleanroom，因此源码渲染代码只能作为行为参考，不能直接复制。

## 已做的小步补丁

- `src/main/java/elucent/eidolon/gui/CodexGui.java` 现在绘制符号和符文图标时，会先正常绘制一次，再通过现有 `LegacyShaders.beginSprite` 叠加一层轻量发光。
- 因为符号/符文列表、详情页、吟唱 lore 序列和当前吟唱栏共用同一组 icon helper，这次补丁会覆盖这些入口。
- 发光叠加受现有 legacy shader 支持和客户端视觉效果开关保护；shader 不可用或视觉效果关闭时，会回退到原本的普通绘制。
- GUI 发光绘制会恢复进入前的 blend 状态，避免影响后续 GUI 元素。

## 仍保留的差异

- 源码吟唱栏通过 `RenderUtil.litQuad`、`MultiBufferSource` 和 glowing sprite shader 绘制；Legacy 这里只复现 GUI 图标上的 additive 视觉提示。
- 源码粒子发光使用自定义 particle `RenderType` 和延迟 level-render flush。1.12 若要复现，需要单独设计渲染阶段或粒子层任务。
- 本次没有添加 mixin，没有改 Gradle 配置，也没有重写全局渲染管线。

## 后续任务

如果后续要求更接近源码粒子辉光，需要单独排一个 1.12 渲染层设计任务：选择非 mixin 的 Forge 渲染 hook，定义哪些粒子类进入 additive 批次，并在方块边缘、仪式高密度粒子和黑暗环境中验证 depth-mask 行为。
