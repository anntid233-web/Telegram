import os
import re

file_path = 'TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java'

if not os.path.exists(file_path):
    print(f"❌ Ошибка: Файл {file_path} не найден!")
    exit(1)

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Добавляем переменную aerogramRow рядом с privacyRow
content = re.sub(
    r'(private\s+int\s+privacyRow\s*;)',
    r'\1\n    private int aerogramRow;',
    content
)

# 2. Регистрируем aerogramRow при подсчёте строк в меню
content = re.sub(
    r'(privacyRow\s*=\s*rowCount\+\+;)',
    r'\1\n        aerogramRow = rowCount++;',
    content
)

# 3. Добавляем ячейку в проверку типов (getItemViewType)
content = re.sub(
    r'(position\s*==\s*privacyRow\s*\|\|)',
    r'position == aerogramRow || \1',
    content
)
content = re.sub(
    r'(\|\|\s*position\s*==\s*privacyRow\s*\))',
    r'|| position == aerogramRow \1',
    content
)

# 4. Настраиваем текст и иконку ячейки (onBindViewHolder)
content = re.sub(
    r'(\}\s*else\s*if\s*\(\s*position\s*==\s*privacyRow\s*\)\s*\{\s*[a-zA-Z0-9_]+\.setText[^\}]+})',
    r'} else if (position == aerogramRow) {\n                textCell.setTextAndIcon("AeroGram", R.drawable.msg_settings, true);\n            \1',
    content
)

# 5. Добавляем обработку клика и открытие твоего фрагмента (onItemClick)
content = re.sub(
    r'(presentFragment\(\s*new\s*PrivacyControlActivity\(\)\s*\)\s*;)',
    r'\1\n            } else if (position == aerogramRow) {\n                presentFragment(new AeroGramSettingsActivity());',
    content
)
# Дублируем для других версий исходников
content = re.sub(
    r'(presentFragment\(\s*new\s*PrivacySettingsActivity\(\)\s*\)\s*;)',
    r'\1\n            } else if (position == aerogramRow) {\n                presentFragment(new AeroGramSettingsActivity());',
    content
)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("✅ Скрипт patcher.py успешно отработал! Код внедрён.")
