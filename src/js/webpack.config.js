const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const path = require('path');

module.exports = {
	module: {
		rules: [
			{
				test: /\.js$/,
				exclude: /node_modules/,
				loader: 'babel-loader',

				options: {
					presets: ['env']
				}
			},
			{
				test: /\.(scss|css)$/,

				use: [
					{
						loader: 'style-loader'
					},
					{
						loader: 'css-loader'
					},
					{
						loader: 'sass-loader'
					}
				]
			},
			{
				test: /\.(png|jpg)$/,
				loader: 'file-loader',
				options: {
					name: '[path][name].[ext]'
				}
			}
		]
	},

	entry: {
		app: './app.js'
	},

	output: {
		filename: '[name].js',
		path: path.resolve(__dirname, '../main/resources/webroot/')
	},

	plugins: [
		new HtmlWebpackPlugin({
			template: 'index.html',
		  title: 'PhotoBooth',
		  filename: 'index.html'
		}),
		new CopyWebpackPlugin([
			{from:'./assets/',to:'assets'} 
		])
	  ],

	mode: 'development'
};
