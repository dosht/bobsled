from setuptools import setup

setup(
    name='applepen',
    version='1.0.0',
    description='Processes applepen data',
    author='Moustafa Abdelhamid',
    packages=['applepen'],
    install_requires=[
        'pandas',
        'tqdm',
        'click'
    ],
)
