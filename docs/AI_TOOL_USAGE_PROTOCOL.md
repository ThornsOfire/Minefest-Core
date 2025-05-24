# AI Tool Usage Protocol

**Version**: 1.0  
**Last Updated**: 2025-05-24  
**Purpose**: Prevent documentation formatting corruption through proper tool selection

---

## 🚨 **CRITICAL RULE**

**The `search_replace` tool corrupts markdown formatting. Use `edit_file` for ALL multi-line or formatted content.**

---

## 📋 **Quick Decision Matrix**

| Question | Answer | Tool to Use |
|----------|--------|-------------|
| Multiple lines involved? | Yes | `edit_file` |
| Contains markdown (headers, bullets, code)? | Yes | `edit_file` |
| Replacing entire sections? | Yes | `edit_file` |
| Simple single-line text only? | Yes | `search_replace` |
| Documentation file (.md)? | Yes | `edit_file` |
| When in doubt? | Always | `edit_file` |

---

## ✅ **Safe for search_replace**

- Single line version numbers: `"1.20.4-0.2.3.4"`
- Simple file paths: `/path/to/file.txt`
- Single configuration values: `setting = value`
- One-word replacements: `oldword` → `newword`

---

## ❌ **NEVER use search_replace for**

- Multi-line content (ANY content spanning multiple lines)
- Markdown headers: `## Header`
- Bullet points: `- Item`
- Code blocks: \`\`\`code\`\`\`
- Tables: `| Column |`
- Nested lists or indented content
- Documentation sections
- Changelog entries

---

## 🔧 **Emergency Recovery**

If formatting corruption occurs:

1. **Stop immediately**
2. **Use `edit_file` to fix the formatting**
3. **Commit the fix with message: "Fix: formatting corruption"**
4. **Document the incident**

---

## 📈 **Success Metrics**

- ✅ Zero formatting corruption incidents
- ✅ All documentation maintains proper structure
- ✅ Line breaks preserved in all content
- ✅ Markdown syntax remains intact

---

**Default Rule**: **When in doubt, use `edit_file`**

**Remember**: Better safe than sorry - `edit_file` preserves formatting, `search_replace` destroys it. 