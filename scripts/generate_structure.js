const fs = require('fs');
const path = require('path');

const rootDir = 'd:\\SURE PROMPT';
const outputFile = path.join(rootDir, 'PROJECT_STRUCTURE.md');

const excludeDirs = new Set([
  '.git', '.idea', '.gradle', 'target', 'build', 'node_modules', 
  'bin', 'obj', '.settings', '.vscode', '.next', 'out', 'dist', 
  '.gemini'
]);

function buildTree(dirPath, indent = '') {
  let result = '';
  let items;
  try {
    items = fs.readdirSync(dirPath, { withFileTypes: true });
  } catch (err) {
    return result;
  }

  // Filter and sort items
  items = items.filter(item => !excludeDirs.has(item.name));
  items.sort((a, b) => {
    if (a.isDirectory() && !b.isDirectory()) return -1;
    if (!a.isDirectory() && b.isDirectory()) return 1;
    return a.name.localeCompare(b.name);
  });

  const count = items.length;
  for (let i = 0; i < count; i++) {
    const item = items[i];
    const isLast = i === count - 1;
    const branch = isLast ? '└── ' : '├── ';
    
    result += `${indent}${branch}${item.name}\n`;
    
    if (item.isDirectory()) {
      const newIndent = indent + (isLast ? '    ' : '│   ');
      result += buildTree(path.join(dirPath, item.name), newIndent);
    }
  }
  return result;
}

try {
  let markdownContent = '# Project Structure: SURE PROMPT\n\n';
  markdownContent += '```text\n';
  markdownContent += '.\n';
  markdownContent += buildTree(rootDir);
  markdownContent += '```\n';
  
  fs.writeFileSync(outputFile, markdownContent, 'utf8');
  console.log('Successfully generated PROJECT_STRUCTURE.md');
} catch (err) {
  console.error('Error generating structure:', err);
  process.exit(1);
}
