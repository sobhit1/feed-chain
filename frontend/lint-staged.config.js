module.exports = {
  '*.{js,jsx}': [
    'eslint --fix',
    'prettier --write',
  ],

  '*.{css,scss}': [
    'prettier --write',
  ],

  '*.{json,yml,yaml,md}': [
    'prettier --write',
  ],
};