import React from 'react';

const FileTreeDemo = () => {
    const fileTree = {
        'sureprompt-web': {
            'src': {
                'components': {},
                'assets': {},
                'styles': {},
                'App.tsx': '',
                'index.tsx': '',
            },
            'public': {
                'index.html': '',
                'favicon.ico': '',
            }
        },
        'sureprompt-app': {
            'src': {
                'main': {
                    'java': {
                        'com': {
                            'suresurya': {
                                'sureprompt': {
                                    'MainActivity.java': '',
                                }
                            }
                        }
                    },
                    'res': {
                        'layout': {
                            'activity_main.xml': '',
                        }
                    }
                }
            }
        },
        'assets': {
            'images': {},
            'videos': {},
            'documents': {}
        },
        'scripts': {
            'build.sh': '',
            'deploy.sh': ''
        },
        'docs': {
            'README.md': '',
            'CONTRIBUTING.md': '',
            'CHANGELOG.md': ''
        }
    };

    const renderTree = (node) => (
        <ul>
            {Object.entries(node).map(([key, value]) => (
                <li key={key}>
                    {key}
                    {typeof value === 'object' && renderTree(value)}
                </li>
            ))}
        </ul>
    );

    return (
        <div>
            <h1>File Tree Structure</h1>
            {renderTree(fileTree)}
        </div>
    );
}

export default FileTreeDemo;