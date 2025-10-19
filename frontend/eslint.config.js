import js from '@eslint/js';
import globals from 'globals';
import react from 'eslint-plugin-react';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import prettierConfig from 'eslint-config-prettier';
import jsxA11y from 'eslint-plugin-jsx-a11y';
import importPlugin from 'eslint-plugin-import';

export default [
  {
    ignores: [
      'dist/',
      'node_modules/',
      'build/',
      'coverage/',
      '*.config.js',
      '*.config.cjs',
      '*.config.mjs',
      'vite.config.*',
      'cypress/',
      '.env',
      '.env.*',
      '.DS_Store',
    ],
  },

  js.configs.recommended,

  {
    files: ['**/*.{js,jsx}'],
    plugins: {
      react,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
      'jsx-a11y': jsxA11y,
      import: importPlugin,
    },
    languageOptions: {
      globals: {
        ...globals.browser,
      },
      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
    settings: {
      react: {
        version: 'detect',
      },
    },
    rules: {
      ...react.configs.recommended.rules,
      ...reactHooks.configs.recommended.rules,
      ...jsxA11y.configs.recommended.rules,

      'no-unused-vars': ['error', { varsIgnorePattern: '^[A-Z_]' }],
      'no-console': 'warn',
      'max-len': ['warn', { code: 120, ignoreUrls: true, ignoreStrings: true }],

      'react/react-in-jsx-scope': 'off',
      'react/prop-types': 'off',
      'react/jsx-no-bind': ['warn', { ignoreRefs: true, allowArrowFunctions: true }],

      'react-refresh/only-export-components': 'warn',

      'import/order': [
        'warn',
        {
          groups: ['builtin', 'external', 'internal', 'parent', 'sibling', 'index'],
          'newlines-between': 'always',
          alphabetize: { order: 'asc', caseInsensitive: true },
        },
      ],
    },
  },

  prettierConfig,
];
