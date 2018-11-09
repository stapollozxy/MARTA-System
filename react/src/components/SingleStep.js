import React from 'react';
import $ from 'jquery';
import { Form, Button, message } from 'antd';
import { API_ROOT } from '../constants';
import { Link } from 'react-router-dom';
import {Log} from './Log';
import {LogData} from './LogData';
import {ResponseLogData} from './ResponseLogData';

const FormItem = Form.Item;

class SingleSimForm extends React.Component {
    state = {
        confirmDirty: false,
        autoCompleteResult: [],
    };

    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);
                //var data = new LogData();

                var d = LogData.getData();
                var rld = ResponseLogData.getData();
                var temp = {number: d.length, content:"step once"};
                d.push(temp);
                LogData.setData(d);
                this.props.transferMsg(d, rld);
                var parent = this;
                $.ajax({
                    url: `${API_ROOT}/command`,
                    method: 'POST',
                    data: "step_once"
                }).then(function(response) {
                    message.success("step once");
                    var tmp = {number: rld.length, content:response};
                    rld.push(tmp);
                    //this.props.transferMsg(d, rld).bind(window);
                    parent.props.transferMsg(d, rld);
                }, function(response) {
                    message.error(response.responseText);
                }).catch(function(error) {
                    message.error(error);
                });
                this.props.transferMsg(d, rld);
            }
        });
    }

    checkConfirm = (rule, value, callback) => {
        const form = this.props.form;
        if (value && this.state.confirmDirty) {
            form.validateFields(['confirm'], { force: true });
        }
        callback();
    }

    render() {
        const { getFieldDecorator } = this.props.form;

        const tailFormItemLayout = {
            wrapperCol: {
                xs: {
                    span: 24,
                    offset: 0,
                },
                sm: {
                    span: 16,
                    offset: 8,
                },
            },
        };

        return (
            <Form onSubmit={this.handleSubmit} className="singlesim-form">

                <FormItem {...tailFormItemLayout}>
                    <Button type="primary" htmlType="submit">Single Step</Button>
                </FormItem>
            </Form>
        );
    }
}

export const SingleStep = Form.create()(SingleSimForm);